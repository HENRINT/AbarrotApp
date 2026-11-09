package com.example.abarrotapp.ui.screen.producto

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.abarrotapp.ui.components.LoadingView
import com.example.abarrotapp.ui.viewmodel.ProductoFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoFormScreen(
    productId: String?,
    viewModel: ProductoFormViewModel,
    onSaveSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val categorias = listOf("Abarrotes", "Lácteos", "Enlatados", "Bebidas", "Limpieza")
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(productId) {
        viewModel.initForm(productId)
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onSaveSuccess()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(if (uiState.isEditMode) "Editar Producto" else "Crear Producto WMS")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading && uiState.nombreInput.isEmpty() && uiState.isEditMode) {
            LoadingView(modifier = Modifier.padding(innerPadding))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!uiState.errorMessage.isNullOrEmpty()) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Campo SKU
                OutlinedTextField(
                    value = uiState.skuInput,
                    onValueChange = { viewModel.onSkuChanged(it) },
                    label = { Text("SKU / Clave Interna *") },
                    leadingIcon = { Icon(Icons.Default.QrCode, contentDescription = null) },
                    isError = uiState.skuError != null,
                    supportingText = {
                        uiState.skuError?.let { Text(it) }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Nombre
                OutlinedTextField(
                    value = uiState.nombreInput,
                    onValueChange = { viewModel.onNombreChanged(it) },
                    label = { Text("Nombre del Producto *") },
                    leadingIcon = { Icon(Icons.Default.ShoppingBag, contentDescription = null) },
                    isError = uiState.nombreError != null,
                    supportingText = {
                        uiState.nombreError?.let { Text(it) }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Menú Desplegable Categoría
                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = uiState.categoriaInput,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría *") },
                        leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        categorias.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    viewModel.onCategoriaChanged(cat)
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Precio de Venta
                OutlinedTextField(
                    value = uiState.precioInput,
                    onValueChange = { viewModel.onPrecioChanged(it) },
                    label = { Text("Precio de Venta ($) *") },
                    leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                    isError = uiState.precioError != null,
                    supportingText = {
                        uiState.precioError?.let { Text(it) }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Existencias (Stock Actual)
                OutlinedTextField(
                    value = uiState.existenciasInput,
                    onValueChange = { viewModel.onExistenciasChanged(it) },
                    label = { Text("Existencias (Stock Actual) *") },
                    leadingIcon = { Icon(Icons.Default.Inventory, contentDescription = null) },
                    isError = uiState.existenciasError != null,
                    supportingText = {
                        uiState.existenciasError?.let { Text(it) }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Stock Mínimo para Alerta
                OutlinedTextField(
                    value = uiState.stockMinimoInput,
                    onValueChange = { viewModel.onStockMinimoChanged(it) },
                    label = { Text("Stock Mínimo (Umbral Alerta) *") },
                    leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null) },
                    isError = uiState.stockMinimoError != null,
                    supportingText = {
                        uiState.stockMinimoError?.let { Text(it) } ?: Text("Alerta visual si existencias <= este valor")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Caducidad
                OutlinedTextField(
                    value = uiState.caducidadInput,
                    onValueChange = { viewModel.onCaducidadChanged(it) },
                    label = { Text("Caducidad (ej. YYYY-MM-DD)") },
                    leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.saveProducto() },
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(if (uiState.isEditMode) "Actualizar Producto" else "Guardar Producto")
                    }
                }
            }
        }
    }
}
