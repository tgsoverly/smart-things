import spock.lang.*
import groovy.util.XmlSlurper
import physicalgraph.device.*

class AirScapeWHFSpec extends Specification {

  def script

  def setup(){
    //To import devices types in the SmartThings IDE they need a specific file format
    //That file format causes groovy class exceptions, so we copy the text to a new file.
    def tempFile = new File("build/AirScapeWHF.groovy")
    tempFile.text = new File("devicetypes/tgsoverly/air-scape-whf.src/air-scape-whf.groovy").text

    script = new GroovyScriptEngine( 'build' ).with {
      loadScriptByName( 'AirScapeWHF.groovy' )
    }.newInstance()

    script.metaClass.getPort = { return 80 }
    script.metaClass.getIP = { return "10.0.1.10" }
  }

  def "set level up"(){
    when:
      def upCount = 0
      def device = GroovyMock(Device)
      device.latestValue(_) >> "2"

      script.metaClass.httpGet = {params ->
        if(params.path.contains("dir=1")){
          upCount++
        }
      }
      script.metaClass.getDevice = { return device }

      script.setToLevel(6)
    then:
      upCount==4

  }

  def "set level down"(){
    when:
      def downCount = 0
      def device = GroovyMock(Device)
      device.latestValue(_) >> "7"

      script.metaClass.httpGet = {params ->
        if(params.path.contains("dir=3")){
          downCount++
        }
      }
      script.metaClass.getDevice = { return device }

      script.setToLevel(5)
    then:
      downCount==2

  }

}
