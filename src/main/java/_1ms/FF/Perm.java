/*
    This file is part of the FancyFreeze project, licensed under the
    GNU General Public License v3.0

    Copyright (C) 2024-2026 _1ms (GRX005)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package _1ms.FF;

public enum Perm {
    Freeze("FF.freeze"),
    FreezeAll("FF.freezeall"),
    Bypass("FF.bypass"),
    Reload("FF.reload"),
    Notify("FF.notify");

    public final String s;

    Perm(String perm) {
        this.s = perm;
    }

}
