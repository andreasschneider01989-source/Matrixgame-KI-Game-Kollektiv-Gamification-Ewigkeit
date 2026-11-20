# Second Earth Astronomy Lab — Quest 002

**Status:** educational MODEL and unit tests; no real observations recorded yet.

## Aim
Give agents a reproducible way to predict the Sun's approximate position and the shadow of a vertical gnomon. Compare model predictions with observations made by a human or a future Minecraft/Luanti agent.

## Model
- NOAA fractional-year approximation for solar declination and equation of time.
- Solar elevation and azimuth from latitude and local apparent solar time.
- Shadow length and direction for a vertical stick of known height.
- Civil-clock to apparent-solar-time conversion using longitude and the date's actual UTC offset.

Inputs must identify whether time is civil clock time or local apparent solar time. Do not silently mix them. Longitude is positive east of Greenwich. Azimuth is degrees clockwise from true north. This is an educational approximation, not a precision ephemeris.

## Run
From the repository root:
\`\`\`bash
python -m unittest discover -s second-earth -p "test_*.py" -v
\`\`\`

## First field experiment
1. Place a straight stick vertically on level ground; measure its height.
2. Mark true north/south using a reliable method; magnetic compass readings can be locally biased.
3. Record date, civil time, UTC offset, latitude, longitude, stick height, shadow length and shadow direction.
4. Take measurements every 15–30 minutes around predicted solar noon on a clear day.
5. Repeat at approximately the same solar times on later dates.
6. Compare predicted and observed shadow length and direction; record cloud, slope, stick tilt and measurement uncertainty.

Do not look directly at the Sun. Use only the stick's shadow.

## Expected discoveries
- Daily apparent motion is primarily caused by Earth's west-to-east rotation.
- The shortest daily shadow occurs near local solar noon, when the Sun crosses the local meridian.
- Seasonal change in solar elevation is driven primarily by Earth's approximately 23.44° axial tilt and its orbit, not simply changing Earth–Sun distance.
- The equation of time and longitude explain why civil-clock 12:00 is generally not solar noon.
- An annual series of same-time solar observations reveals the analemma.

## Scope boundaries
The first module does not model atmospheric refraction, terrain horizon, local obstructions, leap-second/UT1 corrections, precise orbital perturbations, lunar ephemerides or weather. The lunar phase module is a future increment. Use a reputable ephemeris or source data when precision is needed.

## Persistence contract
Store each observation as a separate versioned record with:
- experiment_id and observation_id;
- timestamp with timezone/UTC offset;
- coordinates only at the precision needed and only with consent;
- instrument/method, units and uncertainty;
- measured shadow length/direction;
- model version and predicted values;
- source/provenance and reviewer status.

Public demo records must use generic coordinates, never a contributor's precise home location. Preserve raw observations; corrections create new records rather than silently rewriting history.
