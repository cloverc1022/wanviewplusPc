/*
 * This file is part of VLCJ.
 *
 * VLCJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VLCJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2009-2019 Caprica Software Limited.
 */

package uk.co.caprica.vlcj.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.binding.internal.libvlc_instance_t;

/**
 * Base implementation for media players sharing common behaviours.
 */
public abstract class AbstractMediaPlayer {

    /**
     * Log.
     */
    private final Logger logger = LoggerFactory.getLogger(AbstractMediaPlayer.class);

    /**
     * Native library interface.
     */
    protected final LibVlc libvlc;

    /**
     * Libvlc instance.
     */
    protected final libvlc_instance_t instance;

    /**
     * Create a media player.
     *
     * @param libvlc native library interface
     * @param instance libvlc instance
     */
    protected AbstractMediaPlayer(LibVlc libvlc, libvlc_instance_t instance) {
        this.libvlc = libvlc;
        this.instance = instance;
    }

    @Override
    protected void finalize() throws Throwable {
        logger.debug("finalize()");
        logger.debug("Media player has been garbage collected");
        super.finalize();
        // FIXME should this invoke release()?
    }
}
