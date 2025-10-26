README.md - Smarboy App

EN EL SIGUIENTE LINK ENCONTRARA LA DOCUMENTACION DEL PROYECTO Y LA EXPLICACION DEL VIDEO DEL MISMO: https://drive.google.com/drive/folders/1IIVAuuo_aOo2gJBxRNn2gRe2jRW7qynr?usp=sharing


1. Descripción del Proyecto

   
Smarboy App es una herramienta móvil desarrollada para el sistema operativo Android, diseñada para actuar como un asistente integral en actividades culinarias. El sistema combina las capacidades de procesamiento local del dispositivo con servicios remotos (a través de n8n) para ofrecer cuatro módulos especializados: Chef Innovador, ¿Qué Cocino Hoy?, Chef de Voz y Gestor de Inventario.

•	Chef Innovador: Generación de propuestas gastronómicas únicas y gourmet basadas en los ingredientes disponibles del usuario.
•	¿Qué Cocino Hoy?: Oferta de ideas de platillos factibles considerando el inventario actual de la despensa.
•	Chef de Voz: Interacción manos libres mediante comandos de voz para consultas y asistencia culinaria.
•	Gestor de Inventario: Control y administración de productos de cocina, incluyendo planificación mejorada de compras.


2. Instrucciones de Instalación

   
2.1. Procedimiento de Configuración

1.	Obtener Código: Obtener el repositorio del código fuente.
2.	Abrir Proyecto: Iniciar Android Studio y seleccionar la opción para abrir un proyecto existente.
3.	Sincronización: Localizar el directorio del proyecto y aguardar mientras Gradle sincroniza las dependencias automáticamente (el proceso inicial puede tardar varios minutos).
4.	Preparar Dispositivo: Preparar un terminal físico (habilitando las opciones de desarrollador) o configurar un dispositivo virtual.
5.	Ejecución: Elegir el dispositivo de destino y activar la ejecución mediante el icono de reproducción.
6.	Despliegue: La aplicación se construirá e implementará de forma automática en el dispositivo seleccionado.


   3. Explicación de la Integración con n8n

      
3.1. Concepto Operativo

La aplicación se vincula con la herramienta de orquestación n8n para externalizar operaciones de procesamiento que requieren lógica compleja, integración de inteligencia artificial o almacenamiento. Cada módulo de la app se comunica con un punto de entrada dedicado (Webhook) en la instancia de n8n.

3.2. Ciclo de Transmisión de Información
Cuando el usuario almacena una receta o activa un módulo, el ciclo de datos sigue esta secuencia:
1.	La aplicación estructura los datos necesarios en formato JSON.
2.	Transmite la información mediante una solicitud POST al webhook asignado en n8n.
3.	n8n recibe la carga útil y ejecuta el flujo de trabajo correspondiente.
4.	n8n genera una respuesta estructurada en formato JSON.
5.	La aplicación recibe la respuesta y la procesa para presentar los datos al usuario.
   
3.3. Ajustes en n8n

Para una conexión exitosa, es fundamental:
•	Construir flujos de trabajo en n8n incorporando nodos receptores de tipo Webhook.
•	Poner los flujos en estado operativo (en ambiente de pruebas, iniciar manualmente antes de la ejecución).
•	Asegurar que las direcciones de los webhooks en n8n correspondan exactamente con las configuradas en la clase NetworkManager de la aplicación.
•	Garantizar que los flujos generen respuestas en formato JSON con la estructura predefinida que la aplicación espera para su parsing.

3.4. Diagnóstico de Problemas (Troubleshooting)

Todas las interacciones de red quedan registradas en el Logcat del dispositivo.
•	Etiquetas de Log: Los mensajes relevantes se encuentran bajo las etiquetas "NetworkManager" o "N8N".
•	Información Registrada: Los logs exhiben direcciones web, información transmitida, códigos de estado de respuesta y fallas detectadas (errores de conectividad, timeouts o errores de parsing JSON).


4. Requisitos y Dependencias

   
4.1. Requisitos para Construir el Proyecto (Entorno de Desarrollo)

Los requisitos mínimos para el entorno de desarrollo son:
•	Plataforma: Windows, macOS o Linux (versiones actualizadas).
•	Software: Android Studio (versión más reciente), JDK 17, Git.
•	Memoria: 8 GB de RAM como mínimo (16 GB recomendado).
•	Almacenamiento: 10 GB de espacio libre en disco.

4.2. Requisitos para Operar la Aplicación (Entorno de Ejecución)

Los requisitos para el terminal de usuario son:
•	Sistema Operativo: Android Edición 8.0 (Oreo) o posterior.
•	Conexión: Conexión a red inalámbrica (Wi-Fi) o datos celulares (requerido para la interacción con n8n).
•	Almacenamiento: 100 MB disponibles para la instalación.

4.3. Componentes de Software Principales

La aplicación se basa en los siguientes componentes: AndroidX (librerías de retrocompatibilidad), Material Design Components (elementos visuales) y Utilidades Nativas de Java para la comunicación HTTP (HttpURLConnection) y el manejo de formato de datos JSON.

4.4. Servicios de Terceros

Se requiere una instancia de n8n disponible mediante protocolo HTTPS, con flujos de trabajo de automatización preparados para manejar las peticiones de los módulos.


5. Características de Desempeño


El desempeño de la aplicación se caracteriza por métricas bajas y eficientes en consumo de recursos:
•	Peso del Paquete (APK): Se estima entre 8 y 15 MB.
•	Consumo de Memoria (RAM): 50 a 100 MB durante la operación normal.
•	Tráfico de Red: 1 a 5 KB por transacción con n8n (debido a la ligereza del formato JSON).
•	Impacto Energético: Reducido, ya que no mantiene procesos permanentes en segundo plano.


(LAS CAPTURAS DE PANTALLA O GIFS DEMOSTRATIVOS ESTAN EL DOCUMENTO DE LA EXPLICACION DEL PROYECTO YA QUE EN ESTE ARCHIVO README NO SE PUEDEN SUBIR CAP. DE PANTALLA)

