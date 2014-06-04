package asset.pipeline.sencha

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import javax.servlet.ServletContext

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.GrailsPluginManager
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

@Singleton
class SenchaAssetContextHolder implements ApplicationContextAware {

    static ConfigObject rawConfig
    private ApplicationContext ctx
    private static final Map<String, Object> TEST_BEANS = [ : ]


    static String getAssetMapping() {
        def assetMapping = getApplicationContext()?.assetProcessorService?.assetMapping

        if( !assetMapping ) {
            assetMapping = rawConfig?.grails?.assets?.mapping ?: "assets"
            if( assetMapping.contains( "/" ) ) {
                String message = "the property [grails.assets.mapping] can only be one level" +
                        "deep.  For example, 'foo' and 'bar' would be acceptable values, but 'foo/bar' is not"
                throw new IllegalArgumentException( message )
            }
        }

        return assetMapping
    }

    void setApplicationContext( ApplicationContext applicationContext ) {
        ctx = applicationContext
    }

    static ApplicationContext getApplicationContext() {
        getInstance().ctx
    }

    static Object getBean( String name ) {
        TEST_BEANS[ name ] ?: getApplicationContext().getBean( name )
    }

    static GrailsApplication getGrailsApplication() {
        getBean( 'grailsApplication' )
    }

    static ConfigObject getConfig() {
        getGrailsApplication().config
    }

    static ServletContext getServletContext() {
        getBean( 'servletContext' )
    }

    static GrailsPluginManager getPluginManager() {
        getBean( 'pluginManager' )
    }

    // For testing
    static void registerTestBean( String name, bean ) {
        TEST_BEANS[ name ] = bean
    }

    // For testing
    static void unregisterTestBeans() {
        TEST_BEANS.clear()
    }
}
