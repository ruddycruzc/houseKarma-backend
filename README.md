## Seguridad y gestión de credenciales

### Variables de entorno

HouseKarma utiliza variables de entorno para separar las credenciales de la configuración versionada.

- **`.env`:** contiene las variables locales y está excluido del control de versiones mediante `.gitignore`.
- **`application.properties`:** obtiene las credenciales de MySQL mediante variables de entorno.
- **`compose.yaml`:** utiliza las variables de entorno para configurar el contenedor de MySQL.
- **`.env.example`:** documenta las variables necesarias para configurar el proyecto sin incluir credenciales reales.

### Rotación de credenciales

Durante la revisión de seguridad se decidió cambiar las credenciales de MySQL utilizadas en la configuración inicial del proyecto.

Las nuevas credenciales se mantienen fuera del repositorio. La rotación invalida las contraseñas anteriores, pero no elimina los valores almacenados en el historial de Git. La limpieza de dicho historial se realizará de forma independiente.

**Principio de seguridad:** no incluir credenciales reales, tokens ni claves privadas en el código fuente, la documentación o los commits.