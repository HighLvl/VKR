import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.concurrent.SubmissionPublisher


fun main() {
    val publisher = SubmissionPublisher<Unit>()
    publisher.consume {  }
}
