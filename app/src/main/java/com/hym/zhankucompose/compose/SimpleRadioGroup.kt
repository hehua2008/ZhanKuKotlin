package com.hym.zhankucompose.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * @author hehua2008
 * @date 2024/3/14
 */
@Composable
fun <T> SimpleRadioGroup(
    items: ImmutableList<T>,
    modifier: Modifier = Modifier,
    defaultItemIndex: Int = 0,
    orientation: Orientation = Orientation.Horizontal,
    onItemSelected: (selectedItem: T) -> Unit,
    itemContent: @Composable RowScope.(item: T) -> Unit
) {
    var selectedItem by rememberMutableState(items) {
        items[defaultItemIndex]
    }

    val itemsContent: @Composable () -> Unit = {
        items.forEach { item ->
            Row(
                modifier = Modifier.selectable(
                    selected = (item === selectedItem),
                    role = Role.RadioButton,
                    onClick = {
                        selectedItem = item
                        onItemSelected(item)
                    }
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (item === selectedItem),
                    onClick = null // null recommended for accessibility with screenreaders
                )

                itemContent(item)
            }
        }
    }

    when (orientation) {
        Orientation.Vertical -> Column(modifier = modifier.selectableGroup()) { itemsContent() }
        Orientation.Horizontal -> Row(modifier = modifier.selectableGroup()) { itemsContent() }
    }
}

@Preview
@Composable
private fun PreviewSimpleRadioGroup() {
    Column(modifier = Modifier.background(Color.White)) {
        SimpleRadioGroup(
            items = listOf("按钮1", "按钮2").toImmutableList(),
            onItemSelected = {}
        ) { text ->
            Text(
                text = text,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        SimpleRadioGroup(
            items = listOf("按钮3", "按钮4").toImmutableList(),
            orientation = Orientation.Vertical,
            onItemSelected = {}
        ) { text ->
            Text(
                text = text,
                maxLines = 1
            )
        }
    }
}
