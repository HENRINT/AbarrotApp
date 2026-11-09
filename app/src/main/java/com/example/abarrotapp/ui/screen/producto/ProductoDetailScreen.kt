package com.example.abarrotapp.ui.screen.producto

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.abarrotapp.domain.model.Producto
import com.example.abarrotapp.ui.components.ErrorView
import com.example.abarrotapp.ui.components.LoadingView
import com.example.abarrotapp.ui.state.ProductoDetailUiState
import com.example.abarrotapp.ui.viewmodel.ProductoDetailViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoDetailScreen(
    productId: String,
    viewModel: ProductoDetailViewModel,
    onNavigateToEdit: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(productId) {
        viewModel.loadProducto(productId)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Producto WMS") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                actions = {
                    if (uiState is ProductoDetailUiState.Success) {
                        IconButton(onClick = { onNavigateToEdit(productId) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar Producto"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is ProductoDetailUiState.Loading -> {
                LoadingView(modifier = Modifier.padding(innerPadding))
            }
            is ProductoDetailUiState.Error -> {
                ErrorView(
                    message = state.message,
                    onRetry = { viewModel.loadProducto(productId) },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            is ProductoDetailUiState.Success -> {
                ProductoDetailContent(
                    producto = state.producto,
                    onEdit = { onNavigateToEdit(productId) },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
fun ProductoDetailContent(
    producto: Producto,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isStockBajo = producto.existencias <= producto.stockMinimo

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))

                DetailItem(
                    label = "SKU / Clave Interna",
                    value = producto.sku.ifEmpty { "N/A" }
                )
                Spacer(modifier = Modifier.height(8.dp))

                DetailItem(
                    label = "Categoría",
                    value = producto.categoria
                )
                Spacer(modifier = Modifier.height(8.dp))

                DetailItem(
                    label = "Precio de Venta",
                    value = "\$${String.format(Locale.getDefault(), "%.2f", producto.precio)}"
                )
                Spacer(modifier = Modifier.height(8.dp))

                DetailItem(
                    label = "Existencias Actuales",
                    value = "${producto.existencias} unidades"
                )
                Spacer(modifier = Modifier.height(8.dp))

                DetailItem(
                    label = "Stock Mínimo (Umbral)",
                    value = "${producto.stockMinimo} unidades"
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (isStockBajo) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "¡Alerta de Reabastecimiento! Existencias (${producto.existencias}) por debajo del stock mínimo (${producto.stockMinimo}).",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                DetailItem(
                    label = "Fecha de Caducidad",
                    value = producto.caducidad.ifEmpty { "No especificada" }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onEdit,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Editar Producto")
        }
    }
}

@Composable
fun DetailItem(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
