package br.edu.utfpr.trabalhofinal.ui.conta.form

import br.edu.utfpr.trabalhofinal.data.Conta
import java.time.LocalDate

data class FormField<T>(
    val value: T,
    val containsError: Boolean = false,
    val errorMessageCode: Int = 0
) {
    val hasError get(): Boolean = errorMessageCode > 0
    val isValid get(): Boolean = !hasError
}

data class AccountFormState(
    val accountId: Int = 0,
    val isLoading: Boolean = false,
    val account: Conta = Conta(),
    val hasErrorLoading: Boolean = false,
    val hasErrorSaving : Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val accountSaved: Boolean = false,
    val accountPersistedOrRemoved: Boolean = false,
    val messageCode: Int = 0,
    val description: FormField<String> = FormField(value = ""),
    val date: FormField<LocalDate> = FormField(value = LocalDate.now()),
    val amount: FormField<String> = FormField(value = ""),
    val paid: FormField<Boolean> = FormField(value = false),
    val type: FormField<String> = FormField(value = ""),
) {
    val isNewAccount: Boolean get() = accountId <= 0

    val isFormValid: Boolean
        get() = description.isValid &&
                date.isValid &&
                amount.isValid &&
                paid.isValid &&
                type.isValid
}
