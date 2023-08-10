package lmu.gruppe3.wgmanager.common.domain

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class BaseEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    var id: UUID = UUID.randomUUID()

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    lateinit var created: LocalDateTime

    @UpdateTimestamp
    lateinit var updated: LocalDateTime
}
