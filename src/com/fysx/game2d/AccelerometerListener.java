package com.fysx.game2d;
/*
 * *слушаем изменения датчика
 */
public interface AccelerometerListener {
	public void onAccelerationChanged(float x, float y, float z);
	public void onShake(float force);
}
