QUMI PAPEL - SEMANA 1
Análisis y diseño inicial

Contenido de esta carpeta:

1. 01_Semana_1_Analisis_Diseno_Qumi_Papel.docx
   Documento de requisitos, roles, necesidades iniciales, bocetos y modelo de datos explicado.

2. 02_Modelo_Datos_Inicial_HeidiSQL.sql
   Script SQL para importar en HeidiSQL usando MySQL o MariaDB.
   Base de datos creada: qumi_pedidos_semana1

3. QumiPedidos_Semana1/
   Proyecto inicial de IntelliJ con JavaFX.
   Incluye bocetos FXML editables en Scene Builder.
   No es todavía la aplicación completa, solo la preparación del entorno y diseño inicial.

Cómo abrir la base de datos:
1. Abrir HeidiSQL.
2. Conectarse a MySQL o MariaDB.
3. Ir a Archivo > Ejecutar archivo SQL.
4. Seleccionar 02_Modelo_Datos_Inicial_HeidiSQL.sql.
5. Ejecutar el script completo.

Cómo abrir el proyecto en IntelliJ:
1. Abrir IntelliJ IDEA.
2. File > Open.
3. Seleccionar la carpeta QumiPedidos_Semana1.
4. Esperar a que Maven cargue las dependencias.
5. Ejecutar con Maven: javafx:run.

Dónde están los bocetos para Scene Builder:
QumiPedidos_Semana1/src/main/resources/com/qumi/semana1/view/

Archivos FXML incluidos:
- login-boceto.fxml
- dashboard-boceto.fxml
- clientes-boceto.fxml
- productos-boceto.fxml
- pedidos-boceto.fxml
- reparto-boceto.fxml

Estado del trabajo:
Esta carpeta representa solo la semana 1 del proyecto:
- Observación del funcionamiento.
- Requisitos iniciales.
- Diseño de pantallas.
- Modelo de datos inicial.
- Preparación del entorno.

Pendiente para semana 2:
- Programar controladores.
- Conectar las pantallas con MySQL.
- Implementar login por roles.
- Crear CRUD de clientes, productos y pedidos.
- Añadir cambios de estado y vista de reparto funcional.
