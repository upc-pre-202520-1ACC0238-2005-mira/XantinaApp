# 🚀 Guía de Despliegue a Firebase App Distribution

Esta guía te ayudará a desplegar tu aplicación Android Xantina a Firebase App Distribution usando la consola de Firebase.

## 📋 Prerrequisitos

1. **Cuenta de Firebase**: Necesitas tener una cuenta de Google y un proyecto en Firebase
2. **Firebase CLI**: Instalado y configurado
3. **Proyecto Android configurado**: El proyecto Xantina debe estar compilando correctamente

## 🔧 Paso 1: Configurar Firebase en tu proyecto

### 1.1 Crear proyecto en Firebase Console

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Haz clic en **"Agregar proyecto"** o selecciona un proyecto existente
3. Ingresa el nombre del proyecto (ej: "Xantina")
4. Sigue los pasos para crear el proyecto

### 1.2 Agregar app Android al proyecto Firebase

1. En la página de descripción general del proyecto, haz clic en el icono de **Android** 📱
2. Ingresa los siguientes datos:
   - **Nombre del paquete Android**: `com.upc.xantina`
   - **Apodo de la app** (opcional): `Xantina`
   - **Certificado de firma SHA-1** (opcional, para Authentication)
3. Haz clic en **"Registrar app"**

### 1.3 Descargar google-services.json

1. Descarga el archivo `google-services.json`
2. Colócalo en el directorio `app/` de tu proyecto:
   ```
   XantinaApp/app/google-services.json
   ```

### 1.4 Configurar Google Services Plugin

Abre `build.gradle.kts` (nivel proyecto) y agrega:

```kotlin
// Top-level build.gradle.kts
plugins {
    // ... plugins existentes
    alias(libs.plugins.google.services) apply false
}
```

Luego, agrega la versión en `gradle/libs.versions.toml`:

```toml
[versions]
# ... versiones existentes
googleServices = "4.4.2"

[plugins]
# ... plugins existentes
google-services = { id = "com.google.gms.google-services", version.ref = "googleServices" }
```

Finalmente, en `app/build.gradle.kts`:

```kotlin
plugins {
    // ... plugins existentes
    alias(libs.plugins.google.services)
}
```

## 🔐 Paso 2: Autenticarse con Firebase CLI

```bash
# Instalar Firebase CLI (si no lo tienes)
npm install -g firebase-tools

# Iniciar sesión
firebase login

# Verificar proyectos disponibles
firebase projects:list
```

## 📤 Paso 3: Desplegar usando el script

### Opción 1: Usar el script automatizado

```bash
# Dar permisos de ejecución
chmod +x deploy-firebase.sh

# Desplegar versión release
./deploy-firebase.sh release 1

# Desplegar versión debug
./deploy-firebase.sh debug 1
```

### Opción 2: Despliegue manual por consola

1. **Generar el APK**:
   ```bash
   cd XantinaApp
   ./gradlew assembleRelease
   ```

2. **En Firebase Console**:
   - Ve a tu proyecto en [Firebase Console](https://console.firebase.google.com/)
   - En el menú lateral, busca **"App Distribution"**
   - Haz clic en **"Abrir App Distribution"**
   - Si es la primera vez, haz clic en **"Comenzar"**

3. **Subir el APK**:
   - Haz clic en **"Distribuir release"**
   - Selecciona el archivo `app/build/outputs/apk/release/app-release.apk`
   - Completa el formulario:
     - **Release notes**: Descripción de la versión
     - **Testers**: Agrega emails o grupos de testers

4. **Distribuir**:
   - Haz clic en **"Distribuir"**
   - Los testers recibirán un email con el link para descargar

## 📤 Paso 4: Desplegar por CLI (alternativa)

Si prefieres usar solo la CLI:

```bash
# 1. Generar APK
./gradlew assembleRelease

# 2. Distribuir a grupos de testers
firebase appdistribution:distribute app/build/outputs/apk/release/app-release.apk \
  --app YOUR_PROJECT_ID:android:com.upc.xantina \
  --groups "testers,developers" \
  --release-notes "Versión 1.0 - Primera release"

# 3. O distribuir a emails específicos
firebase appdistribution:distribute app/build/outputs/apk/release/app-release.apk \
  --app YOUR_PROJECT_ID:android:com.upc.xantina \
  --testers "tester1@example.com,tester2@example.com" \
  --release-notes "Versión 1.0 - Primera release"
```

**Nota**: Reemplaza `YOUR_PROJECT_ID` con el ID de tu proyecto Firebase.

## 👥 Paso 5: Configurar grupos de testers

Para facilitar las distribuciones futuras:

1. En Firebase Console, ve a **App Distribution**
2. Haz clic en **"Grupos de testers"**
3. Haz clic en **"Agregar grupo"**
4. Crea grupos como:
   - `developers`: Para el equipo de desarrollo
   - `testers`: Para testers beta
   - `qa`: Para el equipo de QA

## 🔍 Paso 6: Verificar el despliegue

1. Ve a **App Distribution** en Firebase Console
2. Verás el release recién subido
3. Los testers aparecerán en la lista
4. Puedes ver quién ha descargado e instalado la app

## 🐛 Solución de problemas

### Error: "App not found"
- Verifica que hayas agregado la app Android en Firebase Console
- Verifica que el `applicationId` en `build.gradle.kts` sea `com.upc.xantina`

### Error: "No se pudo generar el APK"
- Asegúrate de estar en el directorio `XantinaApp`
- Verifica que el proyecto compile: `./gradlew assembleRelease`

### Error: "Permission denied"
- Verifica que tengas permisos de administrador en el proyecto Firebase
- Verifica que estés autenticado: `firebase login`

### Error: Firebase CLI no encontrado
```bash
# Instalar globalmente
npm install -g firebase-tools

# O usar npx
npx firebase-tools login
```

## 📚 Recursos adicionales

- [Documentación de Firebase App Distribution](https://firebase.google.com/docs/app-distribution)
- [Guía de Firebase CLI](https://firebase.google.com/docs/cli)
- [Configuración de Android](https://firebase.google.com/docs/android/setup)

## ✅ Checklist de despliegue

- [ ] Proyecto creado en Firebase Console
- [ ] App Android registrada en Firebase
- [ ] `google-services.json` descargado y colocado en `app/`
- [ ] Google Services plugin configurado en Gradle
- [ ] Firebase CLI instalado y autenticado
- [ ] APK generado correctamente
- [ ] Grupos de testers configurados (opcional)
- [ ] Release subido y distribuido
- [ ] Testers han recibido el email

---

¡Listo! 🎉 Tu app ahora está disponible para distribución a través de Firebase App Distribution.

