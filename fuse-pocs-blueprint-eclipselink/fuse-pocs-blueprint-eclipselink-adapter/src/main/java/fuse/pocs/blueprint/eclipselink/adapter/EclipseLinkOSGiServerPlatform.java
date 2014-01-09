package fuse.pocs.blueprint.eclipselink.adapter;

import org.eclipse.persistence.platform.server.ServerPlatformBase;
import org.eclipse.persistence.sessions.DatabaseSession;

public class EclipseLinkOSGiServerPlatform extends ServerPlatformBase {

    public EclipseLinkOSGiServerPlatform(DatabaseSession databaseSession) {
        super(databaseSession);
    }

    @Override
    public Class getExternalTransactionControllerClass() {
        return EclipseLinkOSGiTransactionController.class;
    }

}