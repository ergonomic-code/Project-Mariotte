package pro.azhidkov.platform.spring.sdj.refs

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonUnwrapped
import org.springframework.data.jdbc.core.mapping.AggregateReference


data class AggregateReferenceTarget<T : Identifiable<ID>, ID : Any>(
    @JsonUnwrapped val entity: T
) : AggregateReference<T, ID> {

    @JsonIgnore
    override fun getId(): ID = entity.id

}
