package com.nearprop.dto;

import com.nearprop.entity.Reel;
import lombok.Data;

@Data   // 🔥 ye mandatory hai
public class ReelStatusUpdateRequest {

    private Reel.ReelStatus status;  // APPROVED / REJECTED
    private String reason;           // optional
}
