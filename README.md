# M06-UF2-JDBC
La GestioDBHR le da al usuario todas las acciones a realizar posibles y es el main del proyecto consta de 8 acciones:

Antes de realizar estas acciones se pregunta el usuario y contraseña a utilizar, se tiene en cuenta que la url por predeterminado siempre será para mariaDB en el puerto 3306.
## Acción 1: CARREGAR TAULA
Objetivo:
Ejecutar un script SQL que inserta registros en la base de datos desde un archivo externo.
Detalles:
Lee un archivo ubicado en la carpeta db_scripts (específicamente DB_Data_HR.sql) para insertar registros en la tabla correspondiente.
Ignora entradas duplicadas si ya existen en la base de datos.
## Acción 2: CONSULTAR TOTES LES DADES
Objetivo:
Mostrar los registros de la tabla TREN en bloques de 10 registros.
Detalles:
Permite desplazarse por los registros utilizando un offset.
Pregunta al usuario si desea continuar viendo los siguientes 10 registros hasta que no haya más disponibles.
## Acción 3: ALTRES CONSULTES
Objetivo:
Realizar consultas específicas en la tabla TREN.
Opciones:
Consultar un tren por su id.
Buscar trenes que contengan una letra o patrón en su nombre.
## Acción 4: INSERIR NOU REGISTRE
Objetivo:
Insertar un nuevo tren en la tabla TREN.
Detalles:
Solicita al usuario el id, nombre y capacidad del tren.
Valida que el id sea único y superior a 0.
Inserta el nuevo tren en la tabla.
## Acción 5: MODIFICAR UN REGISTRE
Objetivo:
Actualizar los datos de un tren existente en la tabla TREN.
Detalles:
Solicita el id del tren a modificar.
Permite cambiar el nombre y la capacidad del tren asociado a ese id.
## Acción 6: ESBORRAR UN REGISTRE
Objetivo:
Eliminar un tren de la tabla TREN.
Detalles:
Solicita el id del tren a eliminar.
Verifica que el id introducido sea válido.
## Acción 7: GENERAR UN XML DELS REGISTRES
Objetivo:
Crear un archivo XML con los datos de todos los registros de la tabla TREN.
Detalles:
Lee todos los registros de la tabla.
Genera un archivo XML con los datos y lo guarda en la carpeta /xmls.
## Acción 9: SORTIR
Objetivo:
Finalizar la ejecución de la aplicación.
Detalles:
Cambia el valor de la variable sortirapp a true para salir del bucle principal del programa.

El CRUDHR contiene las siguientes funciones destinadas específicamente a los comandos SQL para la obtención de datos.
## Constructor
CRUDHR(Connection conn)
Establece la conexión con la base de datos que se utilizará para realizar operaciones CRUD.
Funciones de creación
boolean CreateDatabase(InputStream input)
Lee sentencias SQL desde un flujo de entrada, como un archivo, e intenta ejecutarlas en la base de datos. Ignora comentarios y líneas vacías, y devuelve true si se detectaron entradas duplicadas en la ejecución.

void InsertTrain(String TableName, Train train)
Inserta un nuevo registro en la tabla especificada utilizando los atributos de un objeto Train (id, nombre y capacidad). Maneja transacciones configurando manualmente el autocommit.

## Funciones de lectura
boolean ReadAllTrains(String TableName, int offset)
Recupera un conjunto limitado de registros de la tabla especificada comenzando desde un desplazamiento (offset). Devuelve true si hay registros adicionales disponibles para ser leídos.

ResultSet ReadAllTrains(String TableName)
Recupera todos los registros de la tabla especificada y devuelve un objeto ResultSet con los resultados.

void ReadTrainsId(String TableName, int id)
Recupera los datos de un registro específico en la tabla especificada cuyo id coincide con el valor proporcionado.

void ReadTrainLike(String TableName, String likeString)
Recupera registros de la tabla especificada cuyos nombres coincidan parcialmente con el patrón proporcionado en likeString.

## Funciones auxiliares
static int getColumnNames(ResultSet rs)
Muestra en consola los nombres de las columnas del ResultSet y devuelve el número total de columnas.

void recorrerRegistres(ResultSet rs, int ColNum)
Itera sobre un ResultSet y muestra en consola los valores de cada registro, separándolos por comas.

## Funciones de actualización
void UpdateTrainId(String TableName, Train train)
Actualiza los valores de un registro específico en la tabla especificada. identifica el registro mediante el id del objeto Train y actualiza sus atributos nombre y capacidad.

## Funciones de eliminación
void DeleteTrainId(String TableName, int id)
Elimina un registro de la tabla especificada cuyo id coincide con el valor proporcionado.

## Funciones de utilidad
int ultimid(String tableName)
Recupera el valor del id más alto de la tabla especificada, que normalmente corresponde al último registro insertado.

Por último el Fichero está destinado solo a crear el XML a partir de un ResulSet:

## crearXML(ResultSet rs)
Genera un archivo XML a partir de los datos obtenidos en un objeto ResultSet.
Crea un documento XML con el nodo raíz <trens>.
Itera sobre el ResultSet, y por cada registro crea un elemento <tren> con un atributo id.

Guarda el archivo XML generado en la carpeta xmls, utilizando un nombre dinámico basado en la fecha y hora actuales.

## CrearElement(String dadaTren, String valor, Element arrel, Document document)

Añade un nuevo elemento al documento XML.
Crea un elemento con el nombre especificado en este caso por dadaTren(nom, capacitat).