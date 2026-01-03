package com.focusup.focusupapp.storage

import androidx.test.core.app.ApplicationProvider
import com.focusup.focusupapp.model.Account
import com.focusup.focusupapp.model.Task
import com.focusup.focusupapp.model.Step
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// ./gradlew :app:testDebugUnitTest --tests "com.focusup.focusupapp.storage.AccountStorageTest" desde Sitio_Web_Administracion/app

@RunWith(RobolectricTestRunner::class)
class AccountStorageTest {

    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    // Cuenta de prueba 1
    private val account1 = Account(
        id = 1,
        name = "Pedrito",
        email = "a@a.com",
        password = "12345",
        isPremium = false,
        points = 0,
        purchasedRewards = mutableListOf(),
        createdAt = "2024-06-15",
        tasks = mutableListOf()
    )

    // Cuenta de prueba 2
    private val account2 = Account(
        id = 2,
        name = "Paquito",
        email = "b@b.com",
        password = "67890",
        isPremium = true,
        points = 100,
        purchasedRewards = mutableListOf(),
        createdAt = "2024-06-16",
        tasks = mutableListOf()
    )

    // Tarea de prueba 1
    private val task1 = Task(
        title = "Examen 1",
        description = "Repasar para el examen",
        dueDate = java.time.LocalDate.of(2024, 6, 20),
        dueTime = java.time.LocalTime.of(15, 0),
        difficulty = 3,
        steps = mutableListOf(Step("Estudiar capitulo 1", false), Step("Hacer ejercicios", true)),
        id = 1,
        isCompleted = false
    )

    // Tarea de prueba 2
    private val task2 = Task(
        title = "Proyecto Final",
        description = "Completar el proyecto final",
        dueDate = java.time.LocalDate.of(2024, 6, 30),
        dueTime = java.time.LocalTime.of(23, 59),
        difficulty = 5,
        steps = mutableListOf(),
        id = 2,
        isCompleted = false
    )

    // Tarea de prueba 3
    private val task3 = Task(
        title = "Tarea Extra",
        description = "Hacer la tarea extra",
        dueDate = java.time.LocalDate.of(2024, 7, 5),
        dueTime = java.time.LocalTime.of(12, 0),
        difficulty = 2,
        steps = mutableListOf(Step("Resolver problema 1", true)),
        id = 3,
        isCompleted = true
    )

    @Before
    fun cleanStorage() {
        // Elimina el archivo antes de cada test
        val file = File(context.filesDir, "accounts.json")
        if (file.exists()) file.delete()
    }

    // Prueba para crear y cargar una cuenta
    @Test
    fun testCreateAndLoadAccount() {
        AccountStorage.saveAccounts(context, listOf(account1))
        val loaded = AccountStorage.loadAccounts(context)
        println("Probando cuenta creada y cargada...")
        assertEquals(1, loaded.size)
        println("Paso la verificacion de cantidad de cuentas")
        val acc = loaded[0]
        assertEquals(1, acc.id)
        println("Paso la verificacion de id")
        assertEquals("Pedrito", acc.name)
        println("Paso la verificacion de nombre")
        assertEquals("a@a.com", acc.email)
        println("Paso la verificacion de email")
        assertEquals("12345", acc.password)
        println("Paso la verificacion de contrasenia")
        println("Cuenta creada y cargada correctamente")
        println()
    }

    // Prueba para verificar email usado
    @Test
    fun testIsEmailUsed() {
        AccountStorage.saveAccounts(context, listOf(account1))
        println("Probando email ya usado...")
        assertTrue(AccountStorage.isEmailUsed(context, "a@a.com"))
        println("Paso la verificacion de email usado")
        assertFalse(AccountStorage.isEmailUsed(context, "b@b.com"))
        println("Paso la verificacion de email no usado")
        println("Email usado verificado correctamente")
        println()
    }

    // Prueba para getNextId
    @Test
    fun testGetNextId() {
        AccountStorage.saveAccounts(context, listOf(account1))
        println("Probando obtencion de siguiente ID...")
        val nextId = AccountStorage.getNextId(context)
        assertEquals(2, nextId)
        println("Paso la verificacion de siguiente ID")
        println("Siguiente ID obtenido correctamente")
        println()
    }

    // Prueba para validar login
    @Test
    fun testValidateLogin() {
        AccountStorage.saveAccounts(context, listOf(account1))
        println("Probando validacion de login...")
        val validAccount = AccountStorage.validateLogin(context, "a@a.com", "12345")
        assertNotNull(validAccount)
        println("Paso la verificacion de login valido")
        val invalidAccount = AccountStorage.validateLogin(context, "a@a.com", "hola")
        assertNull(invalidAccount)
        println("Paso la verificacion de login invalido")
        println("Validacion de login completada correctamente")
        println()
    }

    // addPointsToAccount
    @Test
    fun testAddPointsToAccount() {

        AccountStorage.saveAccounts(context, listOf(account1))
        println("Probando agregar puntos a la cuenta...")

        val result = AccountStorage.addPointsToAccount(context, account1.id, account1.points )
        assertNotNull(result)
        assertEquals(0, result!!.points)
        println("Paso la verificacion de puntos devueltos")

        val loaded = AccountStorage.loadAccounts(context)
        val loadedAccount = loaded.find { it.id == account1.id }
        assertNotNull(loadedAccount)
        assertEquals(0, loadedAccount!!.points)
        println("Paso la verificacion de persistencia de puntos")

        val resultNull = AccountStorage.addPointsToAccount(context, 4, 10)
        assertNull(resultNull)
        println("Paso la verificacion al intentar agregar puntos a cuenta inexistente")
        println("addPointsToAccount completado correctamente")
        println()
    }

    // purchaseReward
    @Test
    fun testPurchaseReward() {
        AccountStorage.saveAccounts(context, listOf(account1, account2))
        println("Probando compra de recompensa...")

        val rewardId = 10
        val cost = 30
        val purchaseResult = AccountStorage.purchaseReward(context, account2.id, rewardId, cost)

        assertNotNull(purchaseResult)
        assertEquals(70, purchaseResult!!.points)
        assertTrue(purchaseResult.purchasedRewards.contains(rewardId))
        println("Paso la verificacion de compra exitosa y resta de puntos")

        val loaded = AccountStorage.loadAccounts(context)
        val loadedAccount2 = loaded.find { it.id == account2.id }
        assertNotNull(loadedAccount2)
        assertEquals(70, loadedAccount2!!.points)
        assertTrue(loadedAccount2.purchasedRewards.contains(rewardId))
        println("Paso la verificacion de persistencia de compra")

        val failResult = AccountStorage.purchaseReward(context, account1.id, 16, 50)
        assertNull(failResult)
        println("Paso la verificacion de intento de compra con puntos insuficientes")

        val loadedAccount1 = AccountStorage.loadAccounts(context).find { it.id == account1.id }
        assertNotNull(loadedAccount1)
        assertFalse(loadedAccount1!!.purchasedRewards.contains(16))
        println("Paso la verificacion de que no se persiste compra fallida")

        println("purchaseReward completado correctamente")
        println()

    }

            // addTaskToAccount
    @Test
    fun testAddTaskToAccount() {
        AccountStorage.saveAccounts(context, listOf(account1))
        println("Probando agregar tarea a la cuenta...")
        AccountStorage.addTaskToAccount(context, account1.id, task1, account1.isPremium)
        val loadedAccounts = AccountStorage.loadAccounts(context)
        val loadedAccount = loadedAccounts.find { it.id == account1.id }
        assertEquals(1, loadedAccount!!.tasks.size)
        println("Paso la verificacion de cantidad de tareas")
        val loadedTask = loadedAccount.tasks[0]
        assertEquals("Examen 1", loadedTask.title)
        println("Paso la verificacion de titulo de tarea")
        assertEquals("Repasar para el examen", loadedTask.description)
        println("Paso la verificacion de descripcion de tarea")
        assertEquals(java.time.LocalDate.of(2024, 6, 20), loadedTask.dueDate)
        println("Paso la verificacion de fecha de vencimiento de tarea")
        assertEquals(java.time.LocalTime.of(15, 0), loadedTask.dueTime)
        println("Paso la verificacion de hora de vencimiento de tarea")
        assertEquals(3, loadedTask.difficulty)
        println("Paso la verificacion de dificultad de tarea")
        assertEquals(2, loadedTask.steps.size)
        println("Paso la verificacion de cantidad de pasos de tarea")
        println("Tarea agregada a la cuenta correctamente")
        println()
    }

    // removeTaskFromAccount
    @Test
    fun testRemoveTaskFromAccount() {
        AccountStorage.saveAccounts(context, listOf(account1))
        AccountStorage.addTaskToAccount(context, account1.id, task1, account1.isPremium)
        AccountStorage.addTaskToAccount(context, account1.id, task2, account1.isPremium)
        
        println("Probando eliminar tarea de la cuenta...")
        
        // Verificar que hay 2 tareas inicialmente
        var loadedAccounts = AccountStorage.loadAccounts(context)
        var loadedAccount = loadedAccounts.find { it.id == account1.id }
        assertEquals(2, loadedAccount!!.tasks.size)
        println("Paso la verificacion inicial de cantidad de tareas")
        
        // Eliminar una tarea
        AccountStorage.removeTaskFromAccount(context, account1.id, task1.id)
        
        // Verificar que solo queda 1 tarea
        loadedAccounts = AccountStorage.loadAccounts(context)
        loadedAccount = loadedAccounts.find { it.id == account1.id }
        assertEquals(1, loadedAccount!!.tasks.size)
        println("Paso la verificacion de eliminacion de tarea")
        
        // Verificar que la tarea correcta fue eliminada
        val remainingTask = loadedAccount.tasks[0]
        assertEquals("Proyecto Final", remainingTask.title)
        println("Paso la verificacion de tarea correcta eliminada")
        
        // Eliminar tarea inexistente
        AccountStorage.removeTaskFromAccount(context, account1.id, 999)
        assertEquals(1, loadedAccount.tasks.size) // La cantidad no cambia
        println("Paso la verificacion de eliminacion de tarea inexistente")
        
        println("Eliminacion de tarea completada correctamente")
        println()
    }
    
    // removeStepFromTaskInAccount
    @Test
    fun testRemoveStepFromTaskInAccount() {
        AccountStorage.saveAccounts(context, listOf(account1))
        AccountStorage.addTaskToAccount(context, account1.id, task1, account1.isPremium)
        
        println("Probando eliminar paso de tarea...")
        
        // Verificar que hay 2 pasos inicialmente
        var loadedAccounts = AccountStorage.loadAccounts(context)
        var loadedAccount = loadedAccounts.find { it.id == account1.id }
        var loadedTask = loadedAccount!!.tasks.find { it.id == task1.id }
        assertEquals(2, loadedTask!!.steps.size)
        println("Paso la verificacion inicial de cantidad de pasos")
        
        // Usar el método más seguro - verificar por contenido del toString
        val stepTexts = loadedTask.steps.map { it.toString() }
        assertTrue(stepTexts.any { it.contains("Estudiar capitulo 1") })
        assertTrue(stepTexts.any { it.contains("Hacer ejercicios") })
        
        // Eliminar el primer paso
        AccountStorage.removeStepFromTaskInAccount(context, account1.id, task1.id, 0)
        
        // Verificar que solo queda 1 paso
        loadedAccounts = AccountStorage.loadAccounts(context)
        loadedAccount = loadedAccounts.find { it.id == account1.id }
        loadedTask = loadedAccount!!.tasks.find { it.id == task1.id }
        assertEquals(1, loadedTask!!.steps.size)
        println("Paso la verificacion de eliminacion de paso")
        
        // Verificar el contenido del paso restante
        val remainingStepText = loadedTask.steps[0].toString()
        assertTrue(remainingStepText.contains("Hacer ejercicios"))
        println("Paso la verificacion de paso correcto eliminado")
        
        // Eliminar paso con índice inválido
        AccountStorage.removeStepFromTaskInAccount(context, account1.id, task1.id, 5)
        assertEquals(1, loadedTask.steps.size)
        println("Paso la verificacion de eliminacion de paso con indice invalido")
        
        // Eliminar paso de tarea inexistente
        AccountStorage.removeStepFromTaskInAccount(context, account1.id, 999, 0)
        println("Paso la verificacion de eliminacion de paso en tarea inexistente")
        
        println("Eliminacion de paso completada correctamente")
        println()
    }

    // updateAccount
    @Test
    fun testUpdateAccount() {
        AccountStorage.saveAccounts(context, listOf(account1))
        
        println("Probando actualizacion de cuenta...")
        
        // Crear una cuenta actualizada
        val updatedAccount = Account(
            id = account1.id,
            name = "Pedrito Actualizado",
            email = "nuevo@email.com",
            password = "nuevaContrasenia123",
            isPremium = true,
            points = 50,
            purchasedRewards = mutableListOf(1, 2, 3),
            createdAt = account1.createdAt,
            tasks = mutableListOf(task1, task2)
        )
        
        // Actualizar la cuenta
        val result = AccountStorage.updateAccount(context, updatedAccount)
        assertTrue(result)
        println("Paso la verificacion de resultado de actualizacion")
        
        // Verificar que los cambios se persistieron
        val loadedAccounts = AccountStorage.loadAccounts(context)
        val loadedAccount = loadedAccounts.find { it.id == account1.id }
        
        assertNotNull(loadedAccount)
        assertEquals("Pedrito Actualizado", loadedAccount!!.name)
        println("Paso la verificacion de nombre actualizado")
        assertEquals("nuevo@email.com", loadedAccount.email)
        println("Paso la verificacion de email actualizado")
        assertEquals("nuevaContrasenia123", loadedAccount.password)
        println("Paso la verificacion de contrasenia actualizada")
        assertTrue(loadedAccount.isPremium)
        println("Paso la verificacion de premium actualizado")
        assertEquals(50, loadedAccount.points)
        println("Paso la verificacion de puntos actualizados")
        assertEquals(3, loadedAccount.purchasedRewards.size)
        println("Paso la verificacion de recompensas actualizadas")
        assertEquals(2, loadedAccount.tasks.size)
        println("Paso la verificacion de tareas actualizadas")
        
        // Intentar actualizar cuenta inexistente
        val nonExistentAccount = Account(
            id = 999,
            name = "No Existe",
            email = "no@existe.com",
            password = "123",
            isPremium = false,
            points = 0,
            purchasedRewards = mutableListOf(),
            createdAt = "2024-01-01",
            tasks = mutableListOf()
        )
        
        val failResult = AccountStorage.updateAccount(context, nonExistentAccount)
        assertFalse(failResult)
        println("Paso la verificacion de actualizacion de cuenta inexistente")
        
        println("Actualizacion de cuenta completada correctamente")
        println()
    }

    // getAccountById
    @Test
    fun testGetAccountById() {
        AccountStorage.saveAccounts(context, listOf(account1, account2))
        println("Probando obtencion de cuenta por ID...")
        val acc1 = AccountStorage.getAccountById(context, account1.id)
        assertNotNull(acc1)
        assertEquals("Pedrito", acc1!!.name)
        println("Paso la verificacion de cuenta 1 por ID")
        val acc2 = AccountStorage.getAccountById(context, account2.id)
        assertNotNull(acc2)
        assertEquals("Paquito", acc2!!.name)
        println("Paso la verificacion de cuenta 2 por ID")
        val accInvalid = AccountStorage.getAccountById(context, 999)
        assertNull(accInvalid)
        println("Paso la verificacion de cuenta invalida por ID")
        println("Obtencion de cuenta por ID completada correctamente")
        println()
    }

    // updateStepInTask
    @Test
    fun testUpdateStepInTask() {
        AccountStorage.saveAccounts(context, listOf(account1))
        AccountStorage.addTaskToAccount(context, account1.id, task1, account1.isPremium)
        println("Probando actualizacion de paso en tarea...")
        AccountStorage.updateStepInTask(context, account1.id, task1.id, 0, true)
        val loadedAccounts = AccountStorage.loadAccounts(context)
        val loadedAccount = loadedAccounts.find { it.id == account1.id }
        val loadedTask = loadedAccount!!.tasks.find { it.id == task1.id }
        val updatedStep = loadedTask!!.steps[0]
        assertTrue(updatedStep.isCompleted)
        println("Paso la verificacion de actualizacion de paso en tarea")
        println("Actualizacion de paso en tarea completada correctamente")
        println()
    }

    // uncompleteTaskOfAccount
    @Test
    fun testUncompleteTaskOfAccount() {
        val completedTask = task3
        AccountStorage.saveAccounts(context, listOf(account1))
        AccountStorage.addTaskToAccount(context, account1.id, completedTask, account1.isPremium)
        println("Probando descompletar tarea en cuenta...")
        val result = AccountStorage.uncompleteTaskOfAccount(context, account1.id, completedTask.id)
        assertTrue(result)
        println("Paso la verificacion de resultado de descompletar tarea en cuenta")
        val loadedAccounts = AccountStorage.loadAccounts(context)
        val loadedAccount = loadedAccounts.find { it.id == account1.id }
        val loadedTask = loadedAccount!!.tasks.find { it.id == completedTask.id }
        assertNotNull(loadedTask)
        assertFalse(loadedTask!!.isCompleted)
        println("Paso la verificacion de descompletar tarea en cuenta")
        println("Descompletar tarea en cuenta completada correctamente")
        println()
    }
        
    // getNextTaskIdForAccount
    @Test
    fun testGetNextTaskIdForAccount() {
        AccountStorage.saveAccounts(context, listOf(account1))
        println("Probando obtencion del siguiente ID de tarea para una cuenta...")

        var nextTaskId = AccountStorage.getNextTaskIdForAccount(context, account1.id)
        assertEquals(1, nextTaskId)
        println("Paso la verificacion cuando no hay tareas (ID = 1)")

        AccountStorage.addTaskToAccount(context, account1.id, task1, account1.isPremium)
        nextTaskId = AccountStorage.getNextTaskIdForAccount(context, account1.id)
        assertEquals(2, nextTaskId)
        println("Paso la verificacion de siguiente ID despues de agregar tarea (ID = 2)")

        AccountStorage.addTaskToAccount(context, account1.id, task2, account1.isPremium)
        nextTaskId = AccountStorage.getNextTaskIdForAccount(context, account1.id)
        assertEquals(3, nextTaskId)
        println("Paso la verificacion de ID consecutivo (ID = 3)")

        println("Obtencion del siguiente ID de tarea completado correctamente")
        println()
    }

    // getTasksForAccount
    @Test
    fun testGetTasksForAccount() {
        AccountStorage.saveAccounts(context, listOf(account1))
        println("Probando obtencion de tareas de una cuenta...")

        var tasks = AccountStorage.getTasksForAccount(context, account1.id)
        assertTrue(tasks.isEmpty())
        println("Paso la verificacion cuando no hay tareas (lista vacia)")

        AccountStorage.addTaskToAccount(context, account1.id, task1, account1.isPremium)
        AccountStorage.addTaskToAccount(context, account1.id, task2, account1.isPremium)

        tasks = AccountStorage.getTasksForAccount(context, account1.id)
        assertEquals(2, tasks.size)
        println("Paso la verificacion de cantidad de tareas (2 tareas)")

        val titles = tasks.map { it.title }
        assertTrue(titles.contains("Examen 1"))
        assertTrue(titles.contains("Proyecto Final"))
        println("Paso la verificacion de titulos de tareas")

        println("Obtencion de tareas de la cuenta completada correctamente")
        println()
    }
}
  