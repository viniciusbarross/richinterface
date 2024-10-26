package br.edu.utfpr.trabalhofinal.data

import java.math.BigDecimal
import java.time.LocalDate

data class Conta(
    val id: Int = 0,
    val description: String = "",
    val date: LocalDate = LocalDate.now(),
    val amount: BigDecimal = BigDecimal.ZERO,
    val isPaid: Boolean = false,
    val type: TipoContaEnum = TipoContaEnum.DESPESA
)
