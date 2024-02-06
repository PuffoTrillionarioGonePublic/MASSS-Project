package com.example.greetingcard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TunerAppBar(
    modifier: Modifier = Modifier,
    title: String,
    onNavIconPressed: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                modifier = Modifier.padding(16.dp),
            )
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = "ciao",//stringResource(id = "ciao"),//R.string.navigation_drawer_open),
                modifier = Modifier
                    .clickable(onClick = onNavIconPressed)
            )
        },
        actions = actions,
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
fun TunerAppBarPreview() {
    TunerAppBar(title = "Tuner")
}

