package br.com.hsg.web.support;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class FacesContextMock extends FacesContext {

    private FacesContextMock() {}

    public static FacesContext criar() {
        FacesContext context = mock(FacesContext.class);
        ExternalContext externalContext = mock(ExternalContext.class);
        when(context.getExternalContext()).thenReturn(externalContext);
        setCurrentInstance(context);
        return context;
    }

    public static void liberar() {
        setCurrentInstance(null);
    }
}
