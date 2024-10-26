import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.utfpr.trabalhofinal.R
import br.edu.utfpr.trabalhofinal.ui.conta.form.AccountFormViewModel
import br.edu.utfpr.trabalhofinal.ui.utils.composables.Carregando
import br.edu.utfpr.trabalhofinal.ui.utils.composables.ErroAoCarregar
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountFormScreen(
    modifier: Modifier = Modifier,
    viewModel: AccountFormViewModel = viewModel(),
    snackBarHosState: SnackbarHostState = remember { SnackbarHostState()},
    onAccountSaved: () -> Unit,
    onBackPressed: () -> Unit,
) {
    LaunchedEffect(viewModel.state.accountSaved) {
        if(viewModel.state.accountSaved){
            onAccountSaved()
        }
    }

    val context = LocalContext.current
    LaunchedEffect(snackBarHosState, viewModel.state.hasErrorSaving) {
        if(viewModel.state.hasErrorSaving){
            snackBarHosState.showSnackbar(
                context.getString(R.string.error_saving)
            )
        }
    }

    val contentModifier: Modifier = modifier.fillMaxSize()
    if(viewModel.state.isLoading){
        Carregando(modifier = contentModifier)
    }else if(viewModel.state.hasErrorLoading){
        ErroAoCarregar (
            modifier = contentModifier,
            onTryAgainPressed = {
                viewModel.loadAccount()
            }
        )
    }else {
        Scaffold(
            modifier = modifier,
            topBar = {
                AppBar(
                    isNewAccount = viewModel.state.isNewAccount,
                    onSaveClicked = onAccountSaved,
                    onBackPressed = onBackPressed,
                    isSaving = viewModel.state.isSaving,
                )
            }
        ) { padding ->
            FormContent(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    isNewAccount: Boolean,
    onSaveClicked: () -> Unit,
    onBackPressed: () -> Unit,
    isSaving: Boolean
) {
    TopAppBar(
        title = { Text(if (isNewAccount) "Nova Conta" else "Editar Conta") },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Voltar")
            }
        },
        actions = {
            if (isSaving) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            } else {
                IconButton(onClick = onSaveClicked) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Salvar")
                }
            }
        }
    )
}

@Composable
fun FormContent(modifier: Modifier = Modifier) {
    // Exemplo de dados do formulário
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var isPaid by remember { mutableStateOf(false) }
    var accountType by remember { mutableStateOf("Despesa") }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FormTextField(
            value = name,
            onValueChanged = { name = it },
            label = "Nome",
            keyboardType = KeyboardType.Text,
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
        )

        FormTextField(
            value = amount,
            onValueChanged = { amount = it },
            label = "Valor",
            keyboardType = KeyboardType.Number,
            leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) }
        )

        FormDatePicker(
            label = "Data",
            value = date,
            onValueChanged = { date = it }
        )

        FormCheckbox(
            checked = isPaid,
            onCheckedChange = { isPaid = it },
            label = "Paga"
        )

        FormRadioButton(
            value = "Despesa",
            groupValue = accountType,
            onValueChanged = { accountType = it },
            label = "Despesa"
        )

        FormRadioButton(
            value = "Receita",
            groupValue = accountType,
            onValueChanged = { accountType = it },
            label = "Receita"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChanged,
        label = { Text(label) },
        leadingIcon = leadingIcon,
        readOnly= readOnly,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormDatePicker(
    label: String,
    value: LocalDate,
    onValueChanged: (LocalDate) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = value.toEpochDay() * 24 * 60 * 60 * 1000
    )

    FormTextField(
        value = value.toString(),
        onValueChanged = {},
        label = label,
        readOnly = true,
        leadingIcon = {
            IconButton(onClick = { showPicker = true }) {
                Icon(Icons.Default.DateRange, contentDescription = null)
            }
        }
    )

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onValueChanged(LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000)))
                        showPicker = false
                    }
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun FormCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Spacer(modifier = Modifier.width(8.dp))
        Text(label)
    }
}

@Composable
fun FormRadioButton(
    value: String,
    groupValue: String,
    onValueChanged: (String) -> Unit,
    label: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = value == groupValue, onClick = { onValueChanged(value) })
        Spacer(modifier = Modifier.width(8.dp))
        Text(label)
    }
}
