/*
 * Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package ly.pp.justpiano3.midi;

import java.util.ArrayList;
import java.util.List;

/**
 * Some utilities for MIDI (some stuff is used from javax.sound.midi)
 *
 * @author Florian Bomers
 */
public final class MidiUtil {
    public final static byte MIN_PIANO_MIDI_PITCH = 21;
    public final static byte MAX_PIANO_MIDI_PITCH = 108;
    public final static int DEFAULT_TEMPO_MPQ = 500000; // 120bpm
    public final static int META_END_OF_TRACK_TYPE = 0x2F;
    public final static int META_TEMPO_TYPE = 0x51;

    /**
     * Suppresses default constructor, ensuring non-instantiability.
     */
    private MidiUtil() {
    }

    /**
     * return true if the passed message is Meta End Of Track
     */
    public static boolean isMetaEndOfTrack(MidiMessage midiMsg) {
        // first check if it is a META message at all
        if (midiMsg.getLength() != 3 || midiMsg.getStatus() != MetaMessage.META) {
            return false;
        }
        // now get message and check for end of track
        byte[] msg = midiMsg.getMessage();
        return ((msg[1] & 0xFF) == META_END_OF_TRACK_TYPE) && (msg[2] == 0);
    }

    /**
     * return if the given message is a meta tempo message
     */
    public static boolean isMetaTempo(MidiMessage midiMsg) {
        // first check if it is a META message at all
        if (midiMsg.getLength() != 6 || midiMsg.getStatus() != MetaMessage.META) {
            return false;
        }
        // now get message and check for tempo
        byte[] msg = midiMsg.getMessage();
        // meta type must be 0x51, and data length must be 3
        return ((msg[1] & 0xFF) == META_TEMPO_TYPE) && (msg[2] == 3);
    }

    /**
     * parses this message for a META tempo message and returns
     * the tempo in MPQ, or -1 if this isn't a tempo message
     */
    public static int getTempoMPQ(MidiMessage midiMsg) {
        // first check if it is a META message at all
        if (midiMsg.getLength() != 6 || midiMsg.getStatus() != MetaMessage.META) {
            return -1;
        }
        byte[] msg = midiMsg.getMessage();
        if (((msg[1] & 0xFF) != META_TEMPO_TYPE) || (msg[2] != 3)) {
            return -1;
        }
        return (msg[5] & 0xFF)
                | ((msg[4] & 0xFF) << 8)
                | ((msg[3] & 0xFF) << 16);
    }

    /**
     * convert tick to microsecond with given tempo.
     * Does not take tempo changes into account.
     * Does not work for SMPTE timing!
     */
    public static long ticks2Microsecond(long tick, double tempoMPQ, int resolution) {
        return (long) (((double) tick) * tempoMPQ / resolution);
    }

    /**
     * Given a tick, convert to microsecond
     *
     * @param cache tempo info and current tempo
     */
    public static long tick2microsecond(Sequence seq, long tick, TempoCache cache) {
        if (seq.getDivisionType() != Sequence.PPQ) {
            double seconds = ((double) tick / (double) (seq.getDivisionType() * seq.getResolution()));
            return (long) (1000000 * seconds);
        }
        if (cache == null) {
            cache = new TempoCache(seq);
        }
        int resolution = seq.getResolution();

        long[] ticks = cache.ticks;
        int[] tempos = cache.tempos; // in MPQ
        int cacheCount = tempos.length;

        // optimization to not always go through entire list of tempo events
        int snapshotIndex = cache.snapshotIndex;
        int snapshotMicro = cache.snapshotMicro;

        // walk through all tempo changes and add time for the respective blocks
        long us = 0; // microsecond

        if (snapshotIndex <= 0
                || snapshotIndex >= cacheCount
                || ticks[snapshotIndex] > tick) {
            snapshotMicro = 0;
            snapshotIndex = 0;
        }
        if (cacheCount > 0) {
            // this implementation needs a tempo event at tick 0!
            int i = snapshotIndex + 1;
            while (i < cacheCount && ticks[i] <= tick) {
                snapshotMicro += ticks2Microsecond(ticks[i] - ticks[i - 1], tempos[i - 1], resolution);
                snapshotIndex = i;
                i++;
            }
            us = snapshotMicro
                    + ticks2Microsecond(tick - ticks[snapshotIndex],
                    tempos[snapshotIndex],
                    resolution);
        }
        cache.snapshotIndex = snapshotIndex;
        cache.snapshotMicro = snapshotMicro;
        return us;
    }

    public static final class TempoCache {
        long[] ticks;
        int[] tempos; // in MPQ
        // index in ticks/tempos at the snapshot
        int snapshotIndex;
        // microsecond at the snapshot
        int snapshotMicro;

        public TempoCache() {
            // just some defaults, to prevents weird stuff
            ticks = new long[1];
            tempos = new int[1];
            tempos[0] = DEFAULT_TEMPO_MPQ;
            snapshotIndex = 0;
            snapshotMicro = 0;
        }

        public TempoCache(Sequence seq) {
            this();
            refresh(seq);
        }

        public synchronized void refresh(Sequence seq) {
            List<MidiEvent> list = new ArrayList<>();
            Track[] tracks = seq.getTracks();
            if (tracks.length > 0) {
                // tempo events only occur in track 0
                Track track = tracks[0];
                int c = track.size();
                for (int i = 0; i < c; i++) {
                    MidiEvent ev = track.get(i);
                    MidiMessage msg = ev.getMessage();
                    if (isMetaTempo(msg)) {
                        // found a tempo event. Add it to the list
                        list.add(ev);
                    }
                }
            }
            int size = list.size() + 1;
            boolean firstTempoIsFake = true;
            if ((size > 1) && (list.get(0).getTick() == 0)) {
                // do not need to add an initial tempo event at the beginning
                size--;
                firstTempoIsFake = false;
            }
            ticks = new long[size];
            tempos = new int[size];
            int e = 0;
            if (firstTempoIsFake) {
                // add tempo 120 at beginning
                ticks[0] = 0;
                tempos[0] = DEFAULT_TEMPO_MPQ;
                e++;
            }
            for (int i = 0; i < list.size(); i++, e++) {
                MidiEvent evt = list.get(i);
                ticks[e] = evt.getTick();
                tempos[e] = getTempoMPQ(evt.getMessage());
            }
            snapshotIndex = 0;
            snapshotMicro = 0;
        }
    }
}
