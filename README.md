
# Puntos extra

## Empleados no disponibles
Cuando el dispatcher no encuentra un empleado disponible para atender la llamada, el mismo la devuelve con estado `BUSY`. El cliente del dispatcher es responsable de decidir que hacer con dicha llamada.

## Dispatcher no disponible
Cuando el dispatcher no está disponible (la cantidad de llamadas actual es mayor a 10) el mismo la devuelve con estado `DISPATCHER_NOT_AVAILABLE`. El cliente del dispatcher es responsable de decidir que hacer con dicha llamada.

# Diseño
En éste diseño, el Dispatcher no es responsable de decidir que hacer con las llamadas que no pueden ser atendidas (ya sea porque no pudo despacharla o porque no se encontró empleados disponibles), en cuyos casos el dispatcher devuelve la llamada con información sobre por qué no pudo ser atendida.

Dicha responsabilidad debería estar en una clase `PhoneExchange` cliente de la clase `Dispatcher`, quien debería encargarse de encolar las llamadas rechazadas por el dispatcher en una `Queue` y manejar los reintentos con un callback del dispatcher. Entiendo que toda ésta lógica queda fuera del scope del ejercicio.

# Tests
Se realizaron tests para los siguientes casos:

- Un call center con 11 empleados (6 operadores, 3 supervisores y 2 directores) que recibe 11 llamadas concurrentes.
- Un call center con 9 empleados (4 operadores, 3 supervisores y 2 directores) que recibe 10 llamadas concurrentes.
- Un call center con 9 empleados (") que recibe **2 tandas secuenciales** de 10 llamadas concurrentes.
- Un call center con 9 empleados (") que recibe 5 llamadas concurrentes. (solo operadores y un supervisor deberían atender)
- Un call center con 9 empleados (") que recibe 4 llamadas concurrentes. (solo operadores deberían atender)
