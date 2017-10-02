/*
 * Copyright (C) 2017 TripNDroid Mobile Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dolby.dax.service;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import java.util.ArrayList;
import tripndroid.dolby.audio.api.DsLog;

public class DsVisualizerManager {

    private static final String TAG = "DsVisualizerManager";

    private static final Object lock_ = new Object();
    private DsCallbackManager cbkManager_ = null;
    private final Runnable cbkOnVisualizerUpdate_ = new Runnable() {
        public void run() {
            DsVisualizerManager.this.visualizerUpdate();
        }
    };
    private DsManager dsManager_ = null;
    private float[] excitations_ = null;
    private float[] gains_ = null;
    private int geqBandCount_ = 20;
    private boolean isVisualizerSuspended_ = false;
    private int noVisualizerCounter_ = 0;
    private int previousVisualizerSize_ = 0;
    private Handler visualizerHandler_;
    private ArrayList<Integer> visualizerList_ = null;
    private HandlerThread visualizerThread_;

    public DsVisualizerManager(DsManager ds, DsCallbackManager cbk) {
        synchronized (lock_) {
            this.dsManager_ = ds;
            this.cbkManager_ = cbk;
            this.visualizerList_ = new ArrayList();
            this.gains_ = new float[this.geqBandCount_];
            this.excitations_ = new float[this.geqBandCount_];
        }
    }

    public void release() {
        synchronized (lock_) {
            if (this.visualizerList_ != null) {
                int size = this.visualizerList_.size();
                for (int i = 0; i < size; i++) {
                    this.visualizerList_.remove(i);
                }
                this.visualizerList_ = null;
            }
            this.gains_ = null;
            this.excitations_ = null;
            this.dsManager_ = null;
            this.cbkManager_ = null;
        }
    }

    public void register(int handle) {
        synchronized (lock_) {
            if (this.visualizerList_ != null) {
                if (this.visualizerList_.size() == 0) {
                    startVisualizer();
                }
                this.visualizerList_.add(new Integer(handle));
                if (this.isVisualizerSuspended_) {
                    invokeVisualizerCbk(5, 0, 1, 0, null, null);
                }
                DsLog.log1("DsVisualizerManager", "Add a visualizer handle " + handle);
            }
        }
    }

    public void unregister(int r8) {
        synchronized (lock_) {
            if (visualizerList_ != null) {
                int size = visualizerList_.size();
                if (size == 0) {
                    DsLog.log1(TAG, "No client registering, do nothing.");
                    return;
                }
                for (int i=0; i<size; i++) {
                    if (r8 == (Integer)visualizerList_.get(i).intValue()) {
                        visualizerList_.remove(i);
                        DsLog.log1(TAG, "remove a visualzier handle "+ r8);
                        int newSize = visualizerList_.size();
                        if(newSize == 0) {
                            // The last visualizer client is unregistering, disable the visualizer.
                            stopVisualizer();
                        }
                        break;
                    }
                }
            }
        }
    }

    private void visualizerUpdate() {
        synchronized (lock_) {
            int len = 0;
            try {
                len = this.dsManager_.getVisualizerData(this.gains_, this.excitations_);
                if (len != this.previousVisualizerSize_) {
                    this.noVisualizerCounter_ = 0;
                }
                this.previousVisualizerSize_ = len;
            } catch (Exception e) {
                Log.e("DsVisualizerManager", "Exception in visualizerUpdate");
                e.printStackTrace();
            }
            if (len == 0) {
                if (!this.isVisualizerSuspended_) {
                    this.noVisualizerCounter_++;
                    if (this.noVisualizerCounter_ >= 10) {
                        this.isVisualizerSuspended_ = true;
                        this.noVisualizerCounter_ = 0;
                        DsLog.log1("DsVisualizerManager", "VISUALIZER_SUSPENDED true");
                        invokeVisualizerCbk(5, 0, 1, 0, null, null);
                    }
                }
            } else if (this.isVisualizerSuspended_) {
                this.noVisualizerCounter_++;
                if (this.noVisualizerCounter_ >= 10) {
                    this.isVisualizerSuspended_ = false;
                    this.noVisualizerCounter_ = 0;
                    DsLog.log1("DsVisualizerManager", "VISUALIZER_SUSPENDED false");
                    invokeVisualizerCbk(5, 0, 0, 0, null, null);
                }
            } else {
                try {
                    if ((this.dsManager_.getDsOn() ? 1 : 0) != 1) {
                        for (int i = 0; i < this.geqBandCount_; i++) {
                            this.gains_[i] = 0.0f;
                            this.excitations_[i] = 0.0f;
                        }
                    }
                } catch (Exception e2) {
                    Log.e("DsVisualizerManager", "Exception found in visualizerUpdate");
                    e2.printStackTrace();
                }
                invokeVisualizerCbk(4, 0, 0, 0, this.gains_, this.excitations_);
            }
            if (this.visualizerHandler_ != null) {
                this.visualizerHandler_.removeCallbacks(this.cbkOnVisualizerUpdate_);
                this.visualizerHandler_.postDelayed(this.cbkOnVisualizerUpdate_, 50);
            }
        }
    }

    private void startVisualizer() {
        synchronized (lock_) {
            try {
                if ((this.dsManager_.getDsOn() ? 1 : 0) == 1) {
                    this.dsManager_.setVisualizerOn(true);
                    if (this.visualizerThread_ == null) {
                        this.visualizerThread_ = new HandlerThread("visualiser thread");
                        this.visualizerThread_.start();
                    }
                    if (this.visualizerHandler_ == null) {
                        Looper looper = this.visualizerThread_.getLooper();
                        if (looper == null) {
                            throw new NullPointerException("VisualizerThread_.getLooper returns null");
                        }
                        this.visualizerHandler_ = new Handler(looper);
                    }
                    this.visualizerHandler_.post(this.cbkOnVisualizerUpdate_);
                    DsLog.log1("DsVisualizerManager", "Visualizer thread is started.");
                } else {
                    DsLog.log1("DsVisualizerManager", "DS is off, will start visualizer thread when it switches to on.");
                }
            } catch (Exception e) {
                Log.e("DsVisualizerManager", "Exception found in startVisualizer");
                e.printStackTrace();
            }
        }
        return;
    }

    private void stopVisualizer() {
        synchronized (lock_) {
            try {
                this.dsManager_.setVisualizerOn(false);
                if (this.visualizerHandler_ != null) {
                    this.visualizerHandler_.getLooper().quit();
                    this.visualizerHandler_ = null;
                    this.visualizerThread_ = null;
                }
            } catch (Exception e) {
                Log.e("DsVisualizerManager", "Exception found in stopVisualizer");
                e.printStackTrace();
            }
            for (int i = 0; i < this.geqBandCount_; i++) {
                this.gains_[i] = 0.0f;
                this.excitations_[i] = 0.0f;
            }
            this.noVisualizerCounter_ = 0;
        }
    }

    public void toggleVisualizer(boolean on) {
        synchronized (lock_) {
            if (this.visualizerList_ != null && this.visualizerList_.size() > 0) {
                if (on) {
                    startVisualizer();
                } else {
                    stopVisualizer();
                    invokeVisualizerCbk(4, 0, 0, 0, this.gains_, this.excitations_);
                }
            }
        }
    }

    private void invokeVisualizerCbk(int what, int handle, int arg1, int arg2, Object obj1, Object obj2) {
        synchronized (lock_) {
            if (this.visualizerList_ != null) {
                try {
                    for (Integer i : this.visualizerList_) {
                        this.cbkManager_.invokeCallback(what, i.intValue(), arg1, arg2, obj1, obj2);
                    }
                } catch (Exception e) {
                    Log.e("DsVisualizerManager", "Exception found in invokeVisualizerCbk");
                    e.printStackTrace();
                }
            }
        }
        return;
    }
}
