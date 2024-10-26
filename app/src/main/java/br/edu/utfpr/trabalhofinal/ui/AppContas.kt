package br.edu.utfpr.trabalhofinal.ui

import AccountFormScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.edu.utfpr.trabalhofinal.data.AccountDatasource
import br.edu.utfpr.trabalhofinal.ui.conta.lista.ListaContasScreen

private object Screens {
    const val LISTA_CONTAS = "listaContas"
    const val FORMULARIO_CONTA = "formularioConta"
    const val DETALHES_CONTA = "detalhesConta"
}

object Arguments {
    const val ID_CONTA = "idConta"
}

private object Routes {
    const val LISTA_CONTAS = Screens.LISTA_CONTAS
    const val FORMULARIO_CONTA = "${Screens.FORMULARIO_CONTA}?${Arguments.ID_CONTA}={${Arguments.ID_CONTA}}"
    const val DETALHES_CONTA = "${Screens.DETALHES_CONTA}/{${Arguments.ID_CONTA}}"
}

@Composable
fun AppContas(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.LISTA_CONTAS
) {
    val contaDatasource = remember { AccountDatasource.instance }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Routes.LISTA_CONTAS) {
            ListaContasScreen(
                onAdicionarPressed = {
                    navController.navigate(Screens.FORMULARIO_CONTA)
                },
                onContaPressed = { conta ->
                    navController.navigate("${Screens.DETALHES_CONTA}/${conta.id}")
                },
            )
        }
        composable(
            route = Routes.DETALHES_CONTA,
            arguments = listOf(navArgument(Arguments.ID_CONTA) { type = NavType.IntType })
        ) {
        }
        composable(
            route = Routes.FORMULARIO_CONTA,
            arguments = listOf(navArgument(Arguments.ID_CONTA) { type = NavType.StringType; nullable = true })
        ) { backStackEntry ->
            val contaId = backStackEntry.arguments?.getInt(Arguments.ID_CONTA)
            val isNewAccount = contaId == null

            AccountFormScreen(

                onBackPressed = { navController.popBackStack() },
                onAccountSaved = {
                    navController.popBackStack(
                        route = Screens.LISTA_CONTAS,
                        inclusive = false
                    )
                },

            )
        }
    }
}
