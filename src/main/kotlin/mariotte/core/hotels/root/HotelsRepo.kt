package pro.azhidkov.mariotte.core.hotels.root

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


/**
 * Репозиторий сущностей "Отель".
 * * Слой в Функциональной архитектуре: императивная оболочка
 * * Тип блока в структурном дизайне: эфферентные и афферентные
 * * Слой в чистой архитектуре: инфраструктура
 * * Тип блока в Эргономичной архитектуре: контейнер ресурса
 */
@Repository
interface HotelsRepo : CrudRepository<Hotel, Int>