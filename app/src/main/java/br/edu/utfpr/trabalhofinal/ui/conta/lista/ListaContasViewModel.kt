package br.edu.utfpr.trabalhofinal.ui.conta.lista

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import br.edu.utfpr.trabalhofinal.data.AccountDatasource
import br.edu.utfpr.trabalhofinal.data.Conta
import br.edu.utfpr.trabalhofinal.data.ContasObserver

class ListaContasViewModel : ViewModel(), ContasObserver {
    var state: ListaContasState by mutableStateOf(ListaContasState())
        private set

    init {
        AccountDatasource.instance.registerObserver(this)
        carregarContas()
    }

    override fun onCleared() {
        AccountDatasource.instance.removeObserver(this)
        super.onCleared()
    }

    fun carregarContas() {
        state = state.copy(
            carregando = true,
            erroAoCarregar = false
        )
        val contas = AccountDatasource.instance.findAll()
        state = state.copy(
            carregando = false,
            contas = contas
        )
    }

    override fun onUpdate(contasAtualizadas: List<Conta>) {
        state = state.copy(contas = contasAtualizadas)
    }
}