package com.shopdarcel.user.entity;

/**
 * User's self-reported gender, optionally provided at registration or
 * later via profile settings. Used for personalized product suggestions.
 */
public enum Gender {
    MALE,
    FEMALE,
    OTHER,
    PREFER_NOT_TO_SAY
}