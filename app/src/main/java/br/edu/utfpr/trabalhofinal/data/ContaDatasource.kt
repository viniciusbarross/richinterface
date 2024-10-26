package br.edu.utfpr.trabalhofinal.data

class AccountDatasource private constructor() {
    companion object {
        val instance: AccountDatasource by lazy {
            AccountDatasource()
        }
    }

    private val accounts: MutableList<Conta> = mutableListOf()
    private val accountObservers: MutableList<ContasObserver> = mutableListOf()

    fun registerObserver(accountObserver: ContasObserver) {
        accountObservers.add(accountObserver)
    }

    fun removeObserver(accountObserver: ContasObserver) {
        accountObservers.remove(accountObserver)
    }

    private fun notifyObservers() {
        accountObservers.forEach { it.onUpdate(findAll()) }
    }

    fun save(account: Conta): Conta = if (account.id > 0) {
        val index = accounts.indexOfFirst { it.id == account.id }
        account.also { accounts[index] = it }
    } else {
        val maxId = accounts.maxByOrNull { it.id }?.id ?: 0
        account.copy(id = maxId + 1).also { accounts.add(it) }
    }.also {
        notifyObservers()
    }

    fun remove(account: Conta) {
        if (account.id > 0) {
            accounts.removeIf { it.id == account.id }
            notifyObservers()
        }
    }

    fun findAll(): List<Conta> = accounts.toList()

    fun findOne(id: Int): Conta? = accounts.firstOrNull { it.id == id }
}
