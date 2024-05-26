package es.upm.macroscore.domain.mapper

interface Mapper<I, O> {
    fun map(input: I): O
}