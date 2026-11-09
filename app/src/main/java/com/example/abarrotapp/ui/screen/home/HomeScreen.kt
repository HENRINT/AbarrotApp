package com.example.abarrotapp.ui.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.abarrotapp.domain.model.Producto
import com.example.abarrotapp.ui.components.ConfirmDeleteDialog
import com.example.abarrotapp.ui.components.EmptyView
import com.example.abarrotapp.ui.components.ErrorView
import com.example.abarrotapp.ui.components.LoadingView
import com.example.abarrotapp.ui.state.HomeUiState
import com.example.abarrotapp.ui.viewmodel.HomeViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var productoToDelete by remember { mutableStateOf<Producto?>(null) }
    val categoriasList = listOf("Todos", "Abarrotes", "Lácteos", "Enlatados", "Bebidas", "Limpieza")

    productoToDelete?.let { producto ->
        ConfirmDeleteDialog(
            productoNombre = producto.nombre,
            onConfirm = {
                viewModel.deleteProducto(producto.id)
                productoToDelete = null
            },
            onDismiss = {
                productoToDelete = null
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("WMS Almacén & Abarrotes") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { viewModel.logout(onLogout) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar Sesión"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar Producto")
            }
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                LoadingView(modifier = Modifier.padding(innerPadding))
            }
            is HomeUiState.Empty -> {
                EmptyView(
                    message = "No hay productos registrados en el inventario.",
                    modifier = Modifier.padding(innerPadding)
                )
            }
            is HomeUiState.Error -> {
                ErrorView(
                    message = state.message,
                    onRetry = { viewModel.loadProductos() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            is HomeUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // 1. Dashboard KPI Cards
                    KpiDashboardSection(
                        totalProductos = state.totalProductos,
                        valorTotal = state.valorTotalInventario,
                        stockBajoCount = state.productosStockBajoCount
                    )

                    // 2. Barra de Búsqueda
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        placeholder = { Text("Buscar por nombre o SKU...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (state.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Limpiar búsqueda")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    // 3. Filtro por Categorías (Chips)
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categoriasList) { cat ->
                            FilterChip(
                                selected = state.selectedCategory.equals(cat, ignoreCase = true),
                                onClick = { viewModel.onCategorySelected(cat) },
                                label = { Text(cat) }
                            )
                        }
                    }

                    // 4. Lista de Productos
                    if (state.filteredProductos.isEmpty()) {
                        EmptyView(
                            message = "No se encontraron productos coincidentes.",
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = state.filteredProductos,
                                key = { it.id }
                            ) { producto ->
                                WmsProductoItemCard(
                                    producto = producto,
                                    onClick = { onNavigateToDetail(producto.id) },
                                    onEdit = { onNavigateToEdit(producto.id) },
                                    onDelete = { productoToDelete = producto }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KpiDashboardSection(
    totalProductos: Int,
    valorTotal: Double,
    stockBajoCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        KpiCard(
            title = "Catálogo",
            value = "$totalProductos",
            icon = Icons.Default.Inventory2,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.weight(1f)
        )
        KpiCard(
            title = "Valor Stock",
            value = "\$${String.format(Locale.getDefault(), "%.0f", valorTotal)}",
            icon = Icons.Default.MonetizationOn,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.weight(1f)
        )
        KpiCard(
            title = "Stock Bajo",
            value = "$stockBajoCount",
            icon = Icons.Default.Warning,
            containerColor = if (stockBajoCount > 0) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (stockBajoCount > 0) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    icon: ImageVector,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = contentColor
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor
            )
        }
    }
}

@Composable
fun WmsProductoItemCard(
    producto: Producto,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isStockBajo = producto.existencias <= producto.stockMinimo

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (producto.sku.isNotEmpty()) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = producto.sku,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = producto.categoria,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Precio: \$${String.format(Locale.getDefault(), "%.2f", producto.precio)}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "Existencias: ${producto.existencias}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isStockBajo) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (isStockBajo) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.width(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Stock Bajo (mín: ${producto.stockMinimo})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
