package br.edu.utfpr.trabalhofinal.ui.conta.lista

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ThumbDownOffAlt
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.utfpr.trabalhofinal.R
import br.edu.utfpr.trabalhofinal.data.Conta
import br.edu.utfpr.trabalhofinal.data.TipoContaEnum
import br.edu.utfpr.trabalhofinal.ui.theme.TrabalhoFinalTheme
import br.edu.utfpr.trabalhofinal.ui.utils.composables.Carregando
import br.edu.utfpr.trabalhofinal.ui.utils.composables.ErroAoCarregar
import br.edu.utfpr.trabalhofinal.utils.calcularProjecao
import br.edu.utfpr.trabalhofinal.utils.calcularSaldo
import br.edu.utfpr.trabalhofinal.utils.formatar
import java.math.BigDecimal
import java.time.LocalDate

@Composable
fun ListaContasScreen(
    modifier: Modifier = Modifier,
    onAdicionarPressed: () -> Unit,
    onContaPressed: (Conta) -> Unit,
    viewModel: ListaContasViewModel = viewModel()
) {
    val contentModifier: Modifier = modifier.fillMaxSize()
    if (viewModel.state.carregando) {
        Carregando(modifier = contentModifier)
    } else if (viewModel.state.erroAoCarregar) {
        ErroAoCarregar(
            modifier = contentModifier,
            onTryAgainPressed = viewModel::carregarContas,
        )
    } else {
        Scaffold(
            modifier = contentModifier,
            topBar = { AppBar(onAtualizarPressed = viewModel::carregarContas) },
            bottomBar = { BottomBar(contas = viewModel.state.contas) },
            floatingActionButton = {
                FloatingActionButton(onClick = onAdicionarPressed) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.adicionar)
                    )
                }
            }
        ) { paddingValues ->
            val modifierWithPadding = Modifier.padding(paddingValues)
            if (viewModel.state.contas.isEmpty()) {
                ListaVazia(modifier = modifierWithPadding.fillMaxSize())
            } else {
                List(
                    modifier = modifierWithPadding,
                    contas = viewModel.state.contas,
                    onContaPressed = onContaPressed
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    modifier: Modifier = Modifier,
    onAtualizarPressed: () -> Unit
) {
    TopAppBar(
        title = { Text(stringResource(R.string.contas)) },
        modifier = modifier.fillMaxWidth(),
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = MaterialTheme.colorScheme.primary,
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        actions = {
            IconButton(onClick = onAtualizarPressed) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = stringResource(R.string.atualizar)
                )
            }
        }
    )
}

@Composable
private fun ListaVazia(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = stringResource(R.string.lista_vazia_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            modifier = Modifier.padding(8.dp),
            text = stringResource(R.string.lista_vazia_subtitle),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun List(
    modifier: Modifier = Modifier,
    contas: List<Conta>,
    onContaPressed: (Conta) -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(contas) { conta ->
            ListItem(
                modifier = Modifier
                    .clickable { onContaPressed(conta) }
                    .padding(8.dp),
                leadingContent = {
                    val icon = if (conta.isPaid) Icons.Filled.ThumbUp else Icons.Filled.ThumbDownOffAlt
                    val iconTint = if (conta.type == TipoContaEnum.RECEITA) Color(0xFF00984E) else Color(0xFFCF5355)
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint
                    )
                },
                headlineContent = {
                    Text(text = conta.description)
                },
                supportingContent = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = conta.date.formatar(),
                            style = MaterialTheme.typography.bodySmall
                        )
                        val valorText = if (conta.type == TipoContaEnum.DESPESA) "-${conta.amount.formatar()}" else conta.amount.formatar()
                        val valorColor = if (conta.type == TipoContaEnum.RECEITA) Color(0xFF00984E) else Color(0xFFCF5355)
                        Text(
                            text = valorText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = valorColor,
                            textAlign = TextAlign.End
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun BottomBar(
    modifier: Modifier = Modifier,
    contas: List<Conta>
) {
    Column(
        modifier = modifier.background(color = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Totalizador(
            modifier = Modifier.padding(top = 20.dp),
            titulo = stringResource(R.string.saldo),
            valor = contas.calcularSaldo(),
            tipoConta = if (contas.calcularSaldo() >= BigDecimal.ZERO) TipoContaEnum.RECEITA else TipoContaEnum.DESPESA
        )
        Totalizador(
            modifier = Modifier.padding(bottom = 20.dp),
            titulo = stringResource(R.string.previsao),
            valor = contas.calcularProjecao(),
            tipoConta = if (contas.calcularProjecao() >= BigDecimal.ZERO) TipoContaEnum.RECEITA else TipoContaEnum.DESPESA
        )
    }
}

@Composable
fun Totalizador(
    modifier: Modifier = Modifier,
    titulo: String,
    valor: BigDecimal,
    tipoConta: TipoContaEnum,
) {
    val valorColor = if (tipoConta == TipoContaEnum.RECEITA) Color(0xFF00984E) else Color(0xFFCF5355)
    val valorText = if (tipoConta == TipoContaEnum.DESPESA) "-${valor.formatar()}" else valor.formatar()

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
            text = titulo,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(Modifier.size(10.dp))
        Text(
            modifier = Modifier.width(100.dp),
            textAlign = TextAlign.End,
            text = valorText,
            color = valorColor
        )
        Spacer(Modifier.size(20.dp))
    }
}

private fun gerarContas(): List<Conta> = listOf(
    Conta(
        description = "Salário",
        amount = BigDecimal("5000.0"),
        type = TipoContaEnum.RECEITA,
        date = LocalDate.of(2024, 9, 5),
        isPaid = true
    ),
    Conta(
        description = "Aluguel",
        amount = BigDecimal("1500.0"),
        type = TipoContaEnum.DESPESA,
        date = LocalDate.of(2024, 9, 10),
        isPaid = true
    ),
    Conta(
        description = "Condomínio",
        amount = BigDecimal("200.0"),
        type = TipoContaEnum.DESPESA,
        date = LocalDate.of(2024, 9, 15),
        isPaid = false
    )
)
