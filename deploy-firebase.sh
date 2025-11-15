#!/bin/bash

# Script para desplegar la app Xantina a Firebase App Distribution
# Uso: ./deploy-firebase.sh [release|debug] [version]

set -e

BUILD_TYPE=${1:-release}
VERSION_CODE=${2:-1}

echo "🚀 Iniciando despliegue de Xantina a Firebase App Distribution"
echo "=========================================="
echo "Build Type: $BUILD_TYPE"
echo "Version Code: $VERSION_CODE"
echo ""

# Verificar que estamos en el directorio correcto
if [ ! -f "app/build.gradle.kts" ]; then
    echo "❌ Error: Debes ejecutar este script desde el directorio XantinaApp"
    exit 1
fi

# Verificar que Firebase CLI está instalado
if ! command -v firebase &> /dev/null; then
    echo "⚠️  Firebase CLI no está instalado"
    echo "📦 Instalando Firebase CLI..."
    npm install -g firebase-tools
fi

# Verificar si el usuario está logueado en Firebase
if ! firebase projects:list &> /dev/null; then
    echo "🔐 Necesitas autenticarte en Firebase"
    echo "Ejecutando: firebase login"
    firebase login
fi

# Limpiar builds anteriores
echo "🧹 Limpiando builds anteriores..."
./gradlew clean

# Generar el APK o AAB
if [ "$BUILD_TYPE" = "release" ]; then
    echo "📦 Generando APK de release..."
    ./gradlew assembleRelease
    
    APK_PATH="app/build/outputs/apk/release/app-release.apk"
    
    if [ ! -f "$APK_PATH" ]; then
        echo "❌ Error: No se pudo generar el APK"
        exit 1
    fi
    
    echo "✅ APK generado: $APK_PATH"
    
    # Obtener el nombre del proyecto Firebase
    echo ""
    echo "📋 Selecciona tu proyecto Firebase (o presiona Enter para usar el default):"
    read -r PROJECT_ID
    
    if [ -z "$PROJECT_ID" ]; then
        echo "⚠️  Debes especificar un PROJECT_ID"
        echo "💡 Puedes encontrarlo en Firebase Console > Configuración del proyecto"
        exit 1
    fi
    
    echo ""
    echo "📤 Subiendo APK a Firebase App Distribution..."
    echo "💡 Nota: Si es la primera vez, necesitarás configurar grupos de testers en Firebase Console"
    
    # Obtener grupos de testers (opcional)
    echo ""
    echo "👥 ¿Tienes grupos de testers configurados? (s/n)"
    read -r HAS_GROUPS
    
    if [ "$HAS_GROUPS" = "s" ] || [ "$HAS_GROUPS" = "S" ]; then
        echo "📝 Ingresa los nombres de los grupos separados por comas (ej: testers,developers):"
        read -r GROUPS
        firebase appdistribution:distribute "$APK_PATH" \
            --app "$PROJECT_ID:android:com.upc.xantina" \
            --groups "$GROUPS" \
            --release-notes "Versión $VERSION_CODE - Build $BUILD_TYPE"
    else
        echo "📧 Ingresa los emails de los testers separados por comas:"
        read -r TESTERS
        firebase appdistribution:distribute "$APK_PATH" \
            --app "$PROJECT_ID:android:com.upc.xantina" \
            --testers "$TESTERS" \
            --release-notes "Versión $VERSION_CODE - Build $BUILD_TYPE"
    fi
    
elif [ "$BUILD_TYPE" = "debug" ]; then
    echo "📦 Generando APK de debug..."
    ./gradlew assembleDebug
    
    APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
    
    if [ ! -f "$APK_PATH" ]; then
        echo "❌ Error: No se pudo generar el APK"
        exit 1
    fi
    
    echo "✅ APK generado: $APK_PATH"
    echo "💡 Para desplegar el APK de debug, usa el mismo comando pero con --app [PROJECT_ID]"
else
    echo "❌ Build type inválido. Usa 'release' o 'debug'"
    exit 1
fi

echo ""
echo "✅ ¡Despliegue completado!"
echo "📱 Los testers recibirán un email con el link para descargar la app"
echo "🔗 También puedes ver el release en: https://console.firebase.google.com/project/$PROJECT_ID/appdistribution"

