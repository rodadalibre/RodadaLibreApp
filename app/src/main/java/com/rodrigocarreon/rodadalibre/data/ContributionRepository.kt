package com.rodrigocarreon.rodadalibre.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.rodrigocarreon.rodadalibre.data.model.CreatePlaceRequest
import com.rodrigocarreon.rodadalibre.data.network.PlaceClient
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class ContributionRepository @Inject constructor(
    private val api: PlaceClient,
    @ApplicationContext private val context: Context
){
    suspend fun submitContribution(
        request: CreatePlaceRequest,
        imageUris: List<String>
    ): Boolean {
        try {
            val photoParts = imageUris.mapNotNull { uriString ->
                prepareFilePart(uriString)
            }

            if (photoParts.isEmpty()) return false

            val uploadResponse = api.uploadPhotos(photoParts)

            if (uploadResponse.isSuccessful) {
                val uploadedPhotoIds = uploadResponse.body()?.photos?.map { it.id } ?: emptyList()

                val finalRequest = request.copy(photoIds = uploadedPhotoIds)
                val placeResponse = api.createPlace(finalRequest)

                return placeResponse.isSuccessful
            }
            return false

        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun prepareFilePart(uriString: String): MultipartBody.Part? {
        return try {
            val uri = Uri.parse(uriString)

            var rotationAngle = 0f
            context.contentResolver.openInputStream(uri)?.use { input ->
                val exif = ExifInterface(input)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                rotationAngle = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                    else -> 0f
                }
            }

            // 2. DECODIFICAR EL BITMAP ORIGINAL
            // (Tenemos que abrir un InputStream nuevo porque el anterior ya se consumió leyendo el EXIF)
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (originalBitmap == null) return null

            val maxResolution = 1280
            val scale = Math.min(
                maxResolution.toFloat() / originalBitmap.width,
                maxResolution.toFloat() / originalBitmap.height
            )

            val matrix = Matrix()
            if (scale < 1f) {
                matrix.postScale(scale, scale)
            }
            if (rotationAngle != 0f) {
                matrix.postRotate(rotationAngle)
            }

            val finalBitmap = Bitmap.createBitmap(
                originalBitmap,
                0, 0,
                originalBitmap.width, originalBitmap.height,
                matrix,
                true
            )

            val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            outputStream.flush()
            outputStream.close()

            if (finalBitmap != originalBitmap) {
                originalBitmap.recycle()
            }
            finalBitmap.recycle()

            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("images[]", file.name, requestFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}