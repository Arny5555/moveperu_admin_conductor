package com.example.moveperu_admin_conductor.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moveperu_admin_conductor.model.RolUsuario
import com.example.moveperu_admin_conductor.ui.screens.DetailScreen
import com.example.moveperu_admin_conductor.ui.screens.FormScreen
import com.example.moveperu_admin_conductor.ui.screens.HomeScreen
import com.example.moveperu_admin_conductor.ui.screens.ListScreen
import com.example.moveperu_admin_conductor.ui.screens.LoginScreen
import com.example.moveperu_admin_conductor.ui.screens.PerfilScreen
import com.example.moveperu_admin_conductor.ui.screens.RegistroScreen
import com.example.moveperu_admin_conductor.ui.screens.SplashScreen
import com.example.moveperu_admin_conductor.ui.screens.UsuariosAdminScreen
import com.example.moveperu_admin_conductor.viewmodel.AuthViewModel
import com.example.moveperu_admin_conductor.viewmodel.ViajeViewModel

object Rutas {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val HOME = "home"
    const val LISTA = "lista"
    const val DETALLE = "detalle/{viajeId}"
    const val FORM = "form?viajeId={viajeId}"
    const val USUARIOS = "usuarios"
    const val PERFIL = "perfil"

    fun detalle(viajeId: Int) = "detalle/$viajeId"
    fun form(viajeId: Int? = null) = if (viajeId != null) "form?viajeId=$viajeId" else "form"
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    viajeViewModel: ViajeViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    val authState by authViewModel.state.collectAsState()
    val rolActual = authState.rolActual

    val mostrarBarraInferior = rutaActual in listOf(Rutas.HOME, Rutas.LISTA, Rutas.USUARIOS, Rutas.PERFIL)

    Scaffold(
        bottomBar = {
            if (mostrarBarraInferior) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    NavigationBarItem(
                        selected = rutaActual == Rutas.HOME,
                        onClick = {
                            navController.navigate(Rutas.HOME) {
                                popUpTo(Rutas.HOME) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Inicio") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )

                    NavigationBarItem(
                        selected = rutaActual == Rutas.LISTA,
                        onClick = {
                            navController.navigate(Rutas.LISTA) {
                                popUpTo(Rutas.HOME) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                        label = { Text("Viajes") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )

                    if (rolActual == RolUsuario.ADMINISTRADOR) {
                        NavigationBarItem(
                            selected = rutaActual == Rutas.USUARIOS,
                            onClick = {
                                navController.navigate(Rutas.USUARIOS) {
                                    popUpTo(Rutas.HOME) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(Icons.Default.Group, contentDescription = null) },
                            label = { Text("Usuarios") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }

                    NavigationBarItem(
                        selected = rutaActual == Rutas.PERFIL,
                        onClick = {
                            navController.navigate(Rutas.PERFIL) {
                                popUpTo(Rutas.HOME) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text("Perfil") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.SPLASH,
            modifier = Modifier.padding(padding)
        ) {
            composable(Rutas.SPLASH) {
                SplashScreen(
                    onTerminado = {
                        navController.navigate(Rutas.LOGIN) {
                            popUpTo(Rutas.SPLASH) { inclusive = true }
                        }
                    }
                )
            }

            composable(Rutas.LOGIN) {
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginExitoso = {
                        navController.navigate(Rutas.HOME) {
                            popUpTo(Rutas.LOGIN) { inclusive = true }
                        }
                    },
                    onIrARegistro = {
                        navController.navigate(Rutas.REGISTRO)
                    }
                )
            }

            composable(Rutas.REGISTRO) {
                RegistroScreen(
                    authViewModel = authViewModel,
                    onRegistroExitoso = {
                        navController.navigate(Rutas.HOME) {
                            popUpTo(Rutas.LOGIN) { inclusive = true }
                        }
                    },
                    onVolver = { navController.popBackStack() }
                )
            }

            composable(Rutas.HOME) {
                HomeScreen(
                    authViewModel = authViewModel,
                    viajeViewModel = viajeViewModel,
                    onIrALista = { navController.navigate(Rutas.LISTA) },
                    onIrACrearViaje = { navController.navigate(Rutas.form()) },
                    onIrAGestionUsuarios = { navController.navigate(Rutas.USUARIOS) },
                    onVerDetalleViaje = { viajeId -> navController.navigate(Rutas.detalle(viajeId)) }
                )
            }

            composable(Rutas.LISTA) {
                ListScreen(
                    viajeViewModel = viajeViewModel,
                    onVerDetalle = { viajeId -> navController.navigate(Rutas.detalle(viajeId)) },
                    onCrearViaje = { navController.navigate(Rutas.form()) }
                )
            }

            composable(
                route = Rutas.DETALLE,
                arguments = listOf(navArgument("viajeId") { type = NavType.IntType })
            ) { entrada ->
                val viajeId = entrada.arguments?.getInt("viajeId") ?: 0
                DetailScreen(
                    viajeId = viajeId,
                    viajeViewModel = viajeViewModel,
                    onVolver = { navController.popBackStack() },
                    onEditarViaje = { id -> navController.navigate(Rutas.form(id)) }
                )
            }

            composable(
                route = Rutas.FORM,
                arguments = listOf(navArgument("viajeId") {
                    type = NavType.IntType
                    defaultValue = -1
                })
            ) { entrada ->
                val idArg = entrada.arguments?.getInt("viajeId") ?: -1
                val viajeId = if (idArg == -1) null else idArg
                FormScreen(
                    viajeId = viajeId,
                    viajeViewModel = viajeViewModel,
                    onVolver = { navController.popBackStack() }
                )
            }

            composable(Rutas.USUARIOS) {
                UsuariosAdminScreen(
                    viajeViewModel = viajeViewModel,
                    onVolver = { navController.popBackStack() },
                    onIrARegistro = { navController.navigate(Rutas.REGISTRO) }
                )
            }

            composable(Rutas.PERFIL) {
                PerfilScreen(
                    authViewModel = authViewModel,
                    viajeViewModel = viajeViewModel,
                    onCerrarSesion = {
                        navController.navigate(Rutas.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
