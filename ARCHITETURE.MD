# Prompt: Integración Backend-Frontend Móvil Kotlin con DDD

## Objetivo

Integrar mi backend con mi aplicación móvil Android en Kotlin siguiendo la arquitectura **Domain-Driven Design (DDD)** de forma simplificada y profesional.

---

## Estructura de Capas DDD

### 1. Domain Layer (Capa de Dominio)

**Entidades de negocio:**

```kotlin
data class User(
    val id: String,
    val name: String,
    val email: String
)

data class Product(
    val id: String,
    val name: String,
    val price: Double
)
```

**Repository Interfaces (contratos):**

```kotlin
interface UserRepository {
    suspend fun getUser(id: String): Result<User>
    suspend fun getAllUsers(): Result<List<User>>
}

interface ProductRepository {
    suspend fun getProducts(): Result<List<Product>>
    suspend fun getProductById(id: String): Result<Product>
}
```

---

### 2. Data Layer (Implementación de Repositorios)

**API Service:**

```kotlin
interface ApiService {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: String): UserDto
    
    @GET("products")
    suspend fun getProducts(): List<ProductDto>
}
```

**DTOs (Data Transfer Objects):**

```kotlin
data class UserDto(
    val id: String,
    val name: String,
    val email: String
)

data class ProductDto(
    val id: String,
    val name: String,
    val price: Double
)
```

**Mappers:**

```kotlin
fun UserDto.toDomain() = User(
    id = this.id,
    name = this.name,
    email = this.email
)

fun ProductDto.toDomain() = Product(
    id = this.id,
    name = this.name,
    price = this.price
)
```

**Repository Implementation:**

```kotlin
class UserRepositoryImpl(
    private val apiService: ApiService
) : UserRepository {
    
    override suspend fun getUser(id: String): Result<User> {
        return try {
            val response = apiService.getUser(id)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

---

### 3. Presentation Layer (ViewModels)

**ViewModel:**

```kotlin
class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<UiState<User>>(UiState.Loading)
    val uiState: StateFlow<UiState<User>> = _uiState.asStateFlow()
    
    fun loadUser(userId: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getUser(userId)
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { _uiState.value = UiState.Error(it.message) }
        }
    }
}

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String?) : UiState<Nothing>()
}
```

**Integración en la Vista:**

```kotlin
class UserActivity : AppCompatActivity() {
    
    private val viewModel: UserViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is UiState.Loading -> showLoading()
                    is UiState.Success -> showUser(state.data)
                    is UiState.Error -> showError(state.message)
                }
            }
        }
        
        viewModel.loadUser("123")
    }
}
```

---

## Inyección de Dependencias (Hilt)

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.example.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    fun provideUserRepository(apiService: ApiService): UserRepository {
        return UserRepositoryImpl(apiService)
    }
}
```

---

## Estructura de Carpetas

```
app/
├── domain/
│   ├── model/
│   │   ├── User.kt
│   │   └── Product.kt
│   └── repository/
│       ├── UserRepository.kt
│       └── ProductRepository.kt
│
├── data/
│   ├── remote/
│   │   ├── ApiService.kt
│   │   └── dto/
│   │       ├── UserDto.kt
│   │       └── ProductDto.kt
│   ├── repository/
│   │   ├── UserRepositoryImpl.kt
│   │   └── ProductRepositoryImpl.kt
│   └── mapper/
│       ├── UserMapper.kt
│       └── ProductMapper.kt
│
├── di/
│   ├── NetworkModule.kt
│   └── RepositoryModule.kt
│
└── presentation/
    ├── user/
    │   ├── UserViewModel.kt
    │   ├── UserActivity.kt
    │   └── UserUiState.kt
    └── product/
        ├── ProductViewModel.kt
        └── ProductActivity.kt
```

---

## Output Esperado

Genera la implementación completa con:

1. ✅ **Domain Layer**: Entidades y Repository Interfaces
2. ✅ **Data Layer**: DTOs, Mappers, ApiService y Repository Implementations
3. ✅ **Presentation Layer**: ViewModels con manejo de estados
4. ✅ **DI**: Módulos de Hilt configurados
5. ✅ **Estructura de carpetas** completa

---

