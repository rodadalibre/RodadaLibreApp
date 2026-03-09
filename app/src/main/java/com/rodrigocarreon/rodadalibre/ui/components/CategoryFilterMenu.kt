package com.rodrigocarreon.rodadalibre.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rodrigocarreon.rodadalibre.R

val icon_bikestation = R.drawable.ic_bikestation;
val icon_workshop = R.drawable.ic_workshop;
val icon_store = R.drawable.ic_store;
val icon_wc = R.drawable.ic_wc;

data class CategoryItem(val id: String, val title: String, val icon: Int)

@Composable
fun CategoryFilterMenu(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
){
    val categories = listOf(
        CategoryItem("STATION", "ESTACIONES", icon_bikestation),
        CategoryItem("WORKSHOP", "AGENCIAS", icon_workshop),
        CategoryItem("STORE", "TIENDAS", icon_store),
        CategoryItem("RESTROOM", "BAÑOS", icon_wc)
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(categories){ category ->
            val isSelected = selectedCategory == category.id

            val mainColor = MaterialTheme.colorScheme.primary

            Surface(
                modifier = Modifier.clickable{onCategorySelected(category.id)},
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, mainColor),
                color = if (isSelected) mainColor else Color.White
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(category.icon),
                        contentDescription = category.title,
                        tint = if (isSelected) Color.White else mainColor,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = category.title,
                        color = if (isSelected) Color.White else mainColor,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}