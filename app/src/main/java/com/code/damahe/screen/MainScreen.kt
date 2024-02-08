package com.code.damahe.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.code.damahe.res.R
import com.code.damahe.res.icon.DCodeIcon.ImageVectorIcon
import com.code.damahe.res.icon.MyIcons
import com.code.damahe.material.component.DCodeBackground
import com.code.damahe.material.component.DCodeGradientBackground
import com.code.damahe.material.dialogs.ThemeDialog
import com.code.damahe.material.model.ThemeString
import com.code.damahe.material.theme.DCodeAppTheme
import com.code.damahe.material.theme.GradientColors
import com.code.damahe.material.theme.LocalGradientColors
import com.code.damahe.model.WeatherResponse
import com.code.damahe.viewmodel.WeatherViewModel

@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
fun MainScreen() {

    val shouldShowGradientBackground = true //TODO : add method
    val showThemeSettingsDialog = remember { mutableStateOf(false) }

    val showInputAlertDialog = remember { mutableStateOf(false) }
    val inputLocationName = remember { mutableStateOf(TextFieldValue()) }
    val configuration = LocalConfiguration.current

    if (showThemeSettingsDialog.value) {
        ThemeDialog(
            string = ThemeString(R.string.title_app_theme, R.string.loading, R.string.ok, R.string.brand_default,
                R.string.brand_android, R.string.dynamic_color_preference, R.string.dynamic_color_yes,
                R.string.dynamic_color_no, R.string.dark_mode_preference, R.string.dark_mode_config_system_default,
                R.string.dark_mode_config_light, R.string.dark_mode_config_dark),
            onDismiss = {showThemeSettingsDialog.value = false},
        )
    }

    if (showInputAlertDialog.value) {
        AlertDialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
            onDismissRequest = { showInputAlertDialog.value = false },
            title = {
                Text(
                    text = stringResource(R.string.txt_location),
                    style = MaterialTheme.typography.titleMedium,
                )
            },
            text = {
                TextField(
                    placeholder = { Text(text = stringResource(R.string.txt_add_location_name)) },
                    value = inputLocationName.value,
                    onValueChange = { inputLocationName.value = it },
                    singleLine = true,
                )
            },
            confirmButton = {
                Text(
                    text = stringResource(id = R.string.ok),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clickable {
                            showInputAlertDialog.value = false
                        },
                )
            }
        )
    }

    DCodeBackground {
        DCodeGradientBackground(
            gradientColors = if (shouldShowGradientBackground) {
                LocalGradientColors.current
            } else {
                GradientColors()
            }
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                topBar = {
                    TopAppBar(
                        title = { Text(text = stringResource(id = R.string.app_name)) },
                        navigationIcon = {
                            IconButton(onClick = { showThemeSettingsDialog.value = true }
                            ) {
                                Icon(ImageVectorIcon(MyIcons.Settings).imageVector, contentDescription = stringResource(id = R.string.txt_preferences))
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Transparent,
                        ),
                        actions = {
                            IconButton(onClick = { showInputAlertDialog.value = true }) {
                                Icon(MyIcons.Add, contentDescription = "Add Location Name")
                            }
                        }
                    )
                },
            ) { padding ->
                Row(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .consumeWindowInsets(padding)
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.only(
                                WindowInsetsSides.Horizontal,
                            ),
                        ),
                ) {
                    WeatherMainScreen(locationName = inputLocationName.value.text)
                }
            }
        }
    }
}

@Composable
fun WeatherMainScreen(
    weatherViewModel: WeatherViewModel = hiltViewModel(),
    locationName: String
) {
    val weatherDetails = weatherViewModel.weatherDetailsLiveData.observeAsState().value

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Your Input :- $locationName",
            style = MaterialTheme.typography.titleMedium)
        // on below line adding a spacer.
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            weatherViewModel.fetchWeatherDetails(locationName)
        }) {
            Text(
                text = stringResource(id = R.string.txt_load_data),
                modifier = Modifier.padding(10.dp),
                fontSize = 15.sp
            )
        }
        // on below line adding a spacer.
        Spacer(modifier = Modifier.height(20.dp))
        if (weatherDetails != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8),
            ) {
                Text(modifier = Modifier.padding(5.dp),
                    text = getWeatherDetails(weatherDetails))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "All Details :-",
                style = MaterialTheme.typography.titleMedium)
            Text(text = weatherDetails.toString())
        }
    }
}

fun getWeatherDetails(weatherDetails: WeatherResponse): String {
    val stringBuilder = StringBuilder()

    stringBuilder.append("location.name : ${weatherDetails.location.name} \n")
    stringBuilder.append("location.region : ${weatherDetails.location.region} \n")
    stringBuilder.append("location.country : ${weatherDetails.location.country} \n")
    stringBuilder.append("location.localtime : ${weatherDetails.location.localtime} \n")

    stringBuilder.append("location.temp_c : ${weatherDetails.current.temp_c} \n")
    stringBuilder.append("location.temp_f : ${weatherDetails.current.temp_f}")

    return stringBuilder.toString()
}


@Preview(
    showBackground = true
)
@Composable
fun DefaultPreview() {
    DCodeAppTheme {
        //MainScreen()
    }
}