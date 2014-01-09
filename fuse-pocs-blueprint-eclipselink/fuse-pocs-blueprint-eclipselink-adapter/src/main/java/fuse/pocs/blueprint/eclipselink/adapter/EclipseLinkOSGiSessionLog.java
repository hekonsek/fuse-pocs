package fuse.pocs.blueprint.eclipselink.adapter;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class EclipseLinkOSGiSessionLog extends AbstractSessionLog {

    private static final Logger LOG = LoggerFactory.getLogger(EclipseLinkOSGiSessionLog.class);

    private static final Map<String, Logger> LOGGERS = new HashMap<String, Logger>();

    public EclipseLinkOSGiSessionLog() {
        this(1);
    }

    public EclipseLinkOSGiSessionLog(int level) {
        setLevel(level);
    }

    @Override
    public void log(SessionLogEntry sessionLogEntry) {
        Logger log = getLogger(sessionLogEntry);
        if (log != null) {
            String message = getMessage(sessionLogEntry);
            if (message != null && !message.isEmpty()) {
                int level = sessionLogEntry.getLevel();
                if (level == SEVERE) {
                    log.error(message);
                } else if (level == WARNING) {
                    log.warn(message);
                } else if (level == INFO) {
                    log.info(message);
                } else if (level == FINE) {
                    log.debug(message);
                } else if (level == FINER) {
                    log.trace(message);
                } else if (level == FINEST) {
                    log.trace(message);
                } else if (level == ALL) {
                    log.trace(message);
                } else {
                    log.debug(message);
                }
            }
        }
    }

    private String getMessage(SessionLogEntry sle) {
        return formatMessage(sle);
    }

    private Logger getLogger(SessionLogEntry sle) {
        String ns = sle.getNameSpace();
        if (ns != null && !ns.isEmpty()) {
            ns = ns.toUpperCase();
            if (!LOGGERS.containsKey(ns)) {
                LOGGERS.put(ns, LoggerFactory.getLogger("JPA-" + ns));
            }
            return LOGGERS.get(ns);
        }

        return LOG;
    }

}