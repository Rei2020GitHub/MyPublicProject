/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description: The class used to report touch input events. Defined the constants touch action,
 * the interface get touch pointer info, action, posX and posY.
 */

#ifndef TOUCH_INPUT_EVENT_H
#define TOUCH_INPUT_EVENT_H

#include "Core/Types.h"
#include "Application/Event/InputEvent.h"

NS_CG_BEGIN

enum TouchAction {
    TOUCH_ACTION_UNKNOWN,
    TOUCH_ACTION_DOWN,
    TOUCH_ACTION_UP,
    TOUCH_ACTION_MOVE,
    TOUCH_ACTION_CANCEL,
    TOUCH_ACTION_POINTER_DOWN,
    TOUCH_ACTION_POINTER_UP,
    TOUCH_ACTION_MAX,
};

struct TouchCoord {
    f32 x = 0.0f;
    f32 y = 0.0f;

    TouchCoord(f32 posX, f32 posY)
    {
        x = posX;
        y = posY;
    }
};

class CGKIT_EXPORT TouchInputEvent : public InputEvent {
public:
    TouchInputEvent(u32 touchIndex, const std::vector<u32>& pointers, const std::vector<TouchCoord>& touchCoords,
        TouchAction action, u64 eventTime, u64 downTime);
    ~TouchInputEvent();
    TouchAction GetAction() const;
    u32 GetTouchCount() const;
    u32 GetTouchIndex() const;
    s32 GetTouchId(u32 touchIndex = 0) const;
    f32 GetPosX(u32 touchIndex = 0) const;
    f32 GetPosY(u32 touchIndex = 0) const;
    u64 GetEventTime() const;
    u64 GetDownTime() const;
    const std::vector<TouchCoord>& GetTouchCoords() const;
    const std::vector<u32>& GetTouchPointers() const;

private:
    std::vector<TouchCoord> m_touchCoords;
    std::vector<u32> m_pointers;
    TouchAction m_action = TOUCH_ACTION_MAX;
    u32 m_touchIndex = 0;
    u32 m_touchCount = 0;
    u64 m_eventTime = 0;
    u64 m_downTime = 0;
};

NS_CG_END

#endif
