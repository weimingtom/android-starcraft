using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Drawing;
using System.IO;

namespace PaletteBuider
{
    class Program
    {
        static int saturate(int val, int max, int min)
        {
            return val > max ? max : val < min ? min : val;
        }
        static void Main(string[] args)
        {
            Console.WriteLine("HotCraft Palette Convert Utility");
            if (args.Length < 2)
            {
                Console.WriteLine("Usage: [Source Image File] [Dest Palette File]");
                return;
            }

                
            String filename = args[0];
            Bitmap bmp = (Bitmap)Bitmap.FromFile(filename);

            Color[] cols = new Color[bmp.Height];

            for (int i = 0; i < bmp.Height; i++)
            {
                Color BlackResColor = bmp.GetPixel(0, i);
                Color WhiteResColor = bmp.GetPixel(255, i);

                Color BlackSrcColor = Color.Black;
                Color WhiteSrcColor = Color.White;

                int A = 255 - saturate(WhiteResColor.R - BlackResColor.R, 255, 0);

                int R = saturate(A != 0 ? (BlackResColor.R * 255) / A : 0, 255, 0);
                int G = saturate(A != 0 ? (BlackResColor.G * 255) / A : 0, 255, 0);
                int B = saturate(A != 0 ? (BlackResColor.B * 255) / A : 0, 255, 0);

                cols[i] = Color.FromArgb(A, R, G, B);
            }


            byte[] palette = new byte[256 * 4];

            for (int y = 0; y < bmp.Height; y++)
            {
                palette[4 + y * 4 + 0] = (byte)cols[y].A;
                palette[4 + y * 4 + 1] = (byte)cols[y].R;
                palette[4 + y * 4 + 2] = (byte)cols[y].G;
                palette[4 + y * 4 + 3] = (byte)cols[y].B;
            }

            File.WriteAllBytes(args[1], palette);

        }
    }
}
