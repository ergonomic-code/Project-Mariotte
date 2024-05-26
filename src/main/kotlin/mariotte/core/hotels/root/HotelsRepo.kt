package pro.azhidkov.mariotte.core.hotels.root

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


@Repository
interface HotelsRepo : CrudRepository<Hotel, Int>