package br.edu.utfpr.trabalhofinal.utils

import br.edu.utfpr.trabalhofinal.data.Conta
import br.edu.utfpr.trabalhofinal.data.TipoContaEnum
import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun List<Conta>.calcularSaldo(): BigDecimal = map {
    if (it.isPaid) {
        if (it.type == TipoContaEnum.DESPESA) {
            it.amount.negate()
        } else {
            it.amount
        }
    } else {
        BigDecimal.ZERO
    }
}.sumOf { it }

fun List<Conta>.calcularProjecao(): BigDecimal = map {
    if (it.type == TipoContaEnum.DESPESA) it.amount.negate() else it.amount
}.sumOf { it }

fun BigDecimal.formatar(): String {
    val formatter = DecimalFormat("R$#,##0.00")
    return formatter.format(this)
}

fun LocalDate.formatar(): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    return format(formatter)
}