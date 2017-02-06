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
    script.metaClass.getIp = { return "10.0.1.10" }
    script.metaClass.log = [info:{msg->},error:{msg->},debug:{msg->}]
  }

  def "set level up"(){
    when:
      def upCount = 0
      def device = GroovyMock(Device)
      device.latestValue(_) >> "2"

      script.metaClass.getDevice = { return device }

      def commands = script.setToLevel(6)
    then:
      commands.findAll{it.params.path.contains("dir=1")}.size()==4

  }

  def "set level down"(){
    when:
      def downCount = 0
      def device = GroovyMock(Device)
      device.latestValue(_) >> "7"

      script.metaClass.getDevice = { return device }

      def commands = script.setToLevel(5)
    then:
      commands.findAll{it.params.path.contains("dir=3")}.size()==2

  }

}
