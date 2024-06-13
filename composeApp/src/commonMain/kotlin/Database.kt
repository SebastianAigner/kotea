package mydatabase

import androidx.room.*
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow


fun RoomDatabase.Builder<AppDatabase>.getRoomDatabase(): AppDatabase {
    return this
        .fallbackToDestructiveMigration(dropAllTables = true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

@Database(entities = [TodoEntity::class, Person::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDao(): TodoDao
}

@Dao
interface TodoDao {
    @Insert
    suspend fun insert(item: TodoEntity)

    @Insert
    suspend fun insert(item: Person)

    @Transaction
    suspend fun initUsers() {
        deleteAllPeople()
        insert(Person(name = "Sebastian"))
        insert(Person(name = "Márton"))
    }

    @Query("SELECT * FROM todo WHERE id = :itemId")
    suspend fun getById(itemId: Int): TodoEntity

    @Query("DELETE FROM people")
    suspend fun deleteAllPeople()

    @Query("DELETE FROM todo")
    suspend fun deleteAll()

    @Query("DELETE FROM todo WHERE personId = :personId")
    suspend fun deleteAllTodosForUserId(personId: Long)

    @Query("SELECT count(*) FROM todo")
    suspend fun count(): Int

    @Transaction
    @Query("SELECT * FROM people")
    fun getPeopleWithTodos(): Flow<List<PersonWithTodos>>

    @Query("SELECT * FROM todo")
    fun getAllAsFlow(): Flow<List<TodoEntity>>
}

@Entity(tableName = "people")
data class Person(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = Person::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("personId"),
        onDelete = ForeignKey.CASCADE,
    )],
    tableName = "todo",
)
data class TodoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val personId: Long,
)

data class PersonWithTodos(
    @Embedded
    val person: Person,

    @Relation(
        parentColumn = "id",
        entityColumn = "personId",
    )
    val todos: List<TodoEntity>,
)
