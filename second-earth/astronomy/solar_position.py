"""Educational solar-position and gnomon-shadow model.

Based on the NOAA fractional-year approximation. Inputs use local apparent
solar time (not civil clock time). This is a learning model, not a precision
ephemeris or a substitute for a surveyed observation.
"""
from __future__ import annotations

from datetime import date
import math


def _validate_latitude(latitude_deg: float) -> None:
    if not math.isfinite(latitude_deg) or not -90.0 <= latitude_deg <= 90.0:
        raise ValueError("latitude_deg must be finite and within [-90, 90]")


def solar_declination(day: date, solar_time_hours: float = 12.0) -> float:
    """Return approximate solar declination in degrees."""
    if not math.isfinite(solar_time_hours) or not 0.0 <= solar_time_hours <= 24.0:
        raise ValueError("solar_time_hours must be between 0 and 24")
    year_days = 366 if _is_leap_year(day.year) else 365
    gamma = 2.0 * math.pi / year_days * (
        day.timetuple().tm_yday - 1 + (solar_time_hours - 12.0) / 24.0
    )
    declination_rad = (
        0.006918
        - 0.399912 * math.cos(gamma)
        + 0.070257 * math.sin(gamma)
        - 0.006758 * math.cos(2 * gamma)
        + 0.000907 * math.sin(2 * gamma)
        - 0.002697 * math.cos(3 * gamma)
        + 0.001480 * math.sin(3 * gamma)
    )
    return math.degrees(declination_rad)


def equation_of_time_minutes(day: date, solar_time_hours: float = 12.0) -> float:
    """Approximate apparent-minus-mean solar time, in minutes."""
    if not math.isfinite(solar_time_hours) or not 0.0 <= solar_time_hours <= 24.0:
        raise ValueError("solar_time_hours must be between 0 and 24")
    year_days = 366 if _is_leap_year(day.year) else 365
    gamma = 2.0 * math.pi / year_days * (
        day.timetuple().tm_yday - 1 + (solar_time_hours - 12.0) / 24.0
    )
    return 229.18 * (
        0.000075
        + 0.001868 * math.cos(gamma)
        - 0.032077 * math.sin(gamma)
        - 0.014615 * math.cos(2 * gamma)
        - 0.040849 * math.sin(2 * gamma)
    )


def apparent_solar_time_hours(
    day: date,
    civil_time_hours: float,
    longitude_deg: float,
    utc_offset_hours: float,
) -> float:
    """Convert civil clock time to apparent solar time.

    utc_offset_hours is the actual civil UTC offset for that date (e.g. +1
    for CET or +2 for CEST). Longitude is positive east of Greenwich.
    The result is not wrapped, so values outside [0, 24) indicate an adjacent
    apparent-solar date boundary.
    """
    if not math.isfinite(civil_time_hours) or not 0.0 <= civil_time_hours < 24.0:
        raise ValueError("civil_time_hours must be within [0, 24)")
    if not math.isfinite(longitude_deg) or not -180.0 <= longitude_deg <= 180.0:
        raise ValueError("longitude_deg must be within [-180, 180]")
    if not math.isfinite(utc_offset_hours) or not -14.0 <= utc_offset_hours <= 14.0:
        raise ValueError("utc_offset_hours must be within [-14, 14]")
    eot = equation_of_time_minutes(day, civil_time_hours)
    correction_minutes = eot + 4.0 * longitude_deg - 60.0 * utc_offset_hours
    return civil_time_hours + correction_minutes / 60.0


def solar_position(
    day: date,
    solar_time_hours: float,
    latitude_deg: float,
    gnomon_height_m: float = 1.0,
) -> dict[str, float | None]:
    """Return sun elevation/azimuth and vertical-stick shadow geometry.

    Azimuth is degrees clockwise from true north. Shadow azimuth points
    opposite the Sun. Shadow length is None when the Sun is at/below horizon.
    """
    _validate_latitude(latitude_deg)
    if not math.isfinite(solar_time_hours) or not 0.0 <= solar_time_hours <= 24.0:
        raise ValueError("solar_time_hours must be between 0 and 24")
    if not math.isfinite(gnomon_height_m) or gnomon_height_m <= 0.0:
        raise ValueError("gnomon_height_m must be finite and positive")

    decl = math.radians(solar_declination(day, solar_time_hours))
    latitude = math.radians(latitude_deg)
    hour_angle = math.radians(15.0 * (solar_time_hours - 12.0))

    sin_elevation = (
        math.sin(latitude) * math.sin(decl)
        + math.cos(latitude) * math.cos(decl) * math.cos(hour_angle)
    )
    elevation = math.asin(max(-1.0, min(1.0, sin_elevation)))

    if abs(math.cos(latitude)) < 1e-12:
        azimuth = 0.0
    else:
        azimuth = (
            math.degrees(
                math.atan2(
                    math.sin(hour_angle),
                    math.cos(hour_angle) * math.sin(latitude)
                    - math.tan(decl) * math.cos(latitude),
                )
            )
            + 180.0
        ) % 360.0

    elevation_deg = math.degrees(elevation)
    shadow_length = gnomon_height_m / math.tan(elevation) if elevation > 0.0 else None
    shadow_azimuth = (azimuth + 180.0) % 360.0 if shadow_length is not None else None
    return {
        "solar_declination_deg": math.degrees(decl),
        "solar_elevation_deg": elevation_deg,
        "solar_azimuth_deg": azimuth,
        "shadow_length_m": shadow_length,
        "shadow_azimuth_deg": shadow_azimuth,
    }


def _is_leap_year(year: int) -> bool:
    return year % 4 == 0 and (year % 100 != 0 or year % 400 == 0)
