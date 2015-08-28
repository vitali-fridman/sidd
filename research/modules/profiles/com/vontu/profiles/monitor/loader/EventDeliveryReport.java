// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.loader;

import com.vontu.communication.dataflow.Remotable;
import com.vontu.profiles.common.StatusUpdateResult;
import com.vontu.communication.dataflow.ShipmentError;
import java.util.logging.Level;
import com.vontu.communication.dataflow.ShipmentException;
import java.text.MessageFormat;
import java.util.logging.Logger;
import com.vontu.communication.data.IndexLoadFailureEventRemote;
import com.vontu.monitor.communication.sender.RemotableDeliveryReport;

class EventDeliveryReport implements RemotableDeliveryReport<IndexLoadFailureEventRemote>
{
    private static final Logger _logger;
    
    public void onSuccess(final IndexLoadFailureEventRemote event) {
        EventDeliveryReport._logger.info(MessageFormat.format("Successfully delivered a load failure event for index {0}.", event.getProfileIndexId()));
    }
    
    public void onRecoverableError(final IndexLoadFailureEventRemote event, final ShipmentException exception) {
        EventDeliveryReport._logger.log(Level.FINE, MessageFormat.format("Failed to deliver a load failure event for index {0}.", event.getProfileIndexId()), (Throwable)exception);
    }
    
    public void onUnrecoverableError(final IndexLoadFailureEventRemote event, final ShipmentException exception) {
        if (exception.getShipmentError() == ShipmentError.RECEIVING_REJECTED && exception.getUserSpecifiedInformation() == StatusUpdateResult.UNKNOWN_MONITOR.value()) {
            EventDeliveryReport._logger.log(Level.WARNING, MessageFormat.format("Failed to deliver a load failure event for index {0} because this monitor was removed from the manager.", event.getProfileIndexId()));
        }
        else {
            EventDeliveryReport._logger.log(Level.SEVERE, MessageFormat.format("Failed to deliver a load failure event for index {0}.", event.getProfileIndexId()), (Throwable)exception);
        }
    }
    
    static {
        _logger = Logger.getLogger(EventDeliveryReport.class.getName());
    }
}
