{:two-traffic-lights {:edges-from-trans {:T2c {:R2 1, :S 1}, :T2b {:O2 1}, :T2a {:G2 1}, :T1c {:S 1, :R1 1}, :T1b {:O1 1}, :T1a {:G1 1}}, :edges-to-trans {:R2 {:T2a 1}, :O2 {:T2c 1}, :G2 {:T2b 1}, :S {:T2a 1, :T1a 1}, :O1 {:T1c 1}, :G1 {:T1b 1}, :R1 {:T1a 1}}, :places {:R2 1, :O2 0, :G2 0, :S 0, :O1 1, :G1 0, :R1 0}, :transitions #{:T1b :T2a :T2b :T2c :T1c :T1a}, :props []}}