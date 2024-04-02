package staks

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import java.util.stream.Stream

class StackArguments: ArgumentsProvider {
    override fun provideArguments(p0: ExtensionContext?): Stream<out Arguments> {
        return Stream.of(
            Arguments.of(TreiberStack<Int>(), "TreiberStack"),
            Arguments.of(EBS<Int>(), "EBS")
        )
    }
}