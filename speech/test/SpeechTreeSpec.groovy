import com.AIA.SpeechTree
import spock.lang.Specification

class SpeechTreeSpec extends Specification{
    def "remember"(){
        expect:
        def st = new SpeechTree()
        st.remember();
    }
}
