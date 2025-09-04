package hello.tis.hello_crdt_viewer.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class MyDocument(
    @Id
    val id: String,
    val title: String,
    val sentences: List<Sentence>,
    val lastUpdateSequence: Long,
) {
    fun getOrderedSentences(): List<Sentence> {
        if (sentences.isEmpty()) return emptyList()
        val result = mutableListOf<Sentence>()
        val visited = mutableSetOf<String>()
        val sentencesByPrevId = sentences.groupBy { it.prevId }
        val allIds = sentences.map { it.id }.toSet()
        val rootSentences = sentences
            .filter { it.prevId !in allIds }
            .sortedBy { it.id }
        
        for (rootSentence in rootSentences) {
            if (rootSentence.id !in visited) {
                addSentenceChain(rootSentence, sentencesByPrevId, result, visited)
            }
        }
        
        val remainingSentences = sentences.filter { it.id !in visited }
            .sortedBy { it.id }
        
        for (sentence in remainingSentences) {
            if (sentence.id !in visited) {
                addSentenceChain(sentence, sentencesByPrevId, result, visited)
            }
        }
        
        return result
    }
    
    private fun addSentenceChain(
        currentSentence: Sentence,
        sentencesByPrevId: Map<String, List<Sentence>>,
        result: MutableList<Sentence>,
        visited: MutableSet<String>
    ) {
        if (currentSentence.id in visited) return // Avoid infinite loops
        
        visited.add(currentSentence.id)
        result.add(currentSentence)
        
        val nextSentences = sentencesByPrevId[currentSentence.id]?.sortedBy { it.id } ?: emptyList()
        for (nextSentence in nextSentences) {
            addSentenceChain(nextSentence, sentencesByPrevId, result, visited)
        }
    }
}
