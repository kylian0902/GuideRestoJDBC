package ch.hearc.ig.guideresto.service;

public final class ServiceFactory {
    private static final GuideRestoService INSTANCE = new GuideRestoServiceImpl();
    private ServiceFactory() {}
    public static GuideRestoService get() { return INSTANCE; }
}