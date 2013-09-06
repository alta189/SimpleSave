/*
 * This file is part of SimpleSave
 *
 * SimpleSave is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SimpleSave is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.alta189.simplesave.sqlite;

import com.alta189.simplesave.DatabaseFactory;
import com.alta189.simplesave.Field;
import com.alta189.simplesave.Id;
import com.alta189.simplesave.Table;
import com.alta189.simplesave.exceptions.ConnectionException;
import com.alta189.simplesave.exceptions.TableRegistrationException;
import com.alta189.simplesave.sqlite.SQLiteConfiguration;
import com.alta189.simplesave.sqlite.SQLiteDatabase;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class SQLiteDatabaseTest {

	@Test
	public void test() {
		SQLiteConfiguration config = new SQLiteConfiguration();
		File tmpfile = null;
		try {
			tmpfile = File.createTempFile("h2test_", ".db");
		} catch (IOException e) {
			e.printStackTrace();
			fail("IOException occured: " + e.toString());
		}
		assertNotNull(tmpfile);
		config.setPath(tmpfile.getAbsolutePath().substring(0, tmpfile.getAbsolutePath().indexOf(".db")));
		tmpfile.deleteOnExit();
		SQLiteDatabase db = (SQLiteDatabase) DatabaseFactory.createNewDatabase(config);
		try {
			db.registerTable(TestClass.class);
		} catch (TableRegistrationException e) {
			e.printStackTrace();
			fail("Exception occured too early! " + e.toString());
		}
		try {
			db.registerTable(TestClass2.class);
		} catch (TableRegistrationException e) {
			e.printStackTrace();
			fail("Exception occured too early! " + e.toString());
		}
		try {
			db.connect();
		} catch (ConnectionException e) {
			fail("Failed to connect to database! " + e.toString());
		}
		TestClass one = new TestClass();
		one.setName("Hello World");
		db.save(TestClass.class, one);
		TestClass two = new TestClass();
		two.setName("Cruel World");
		db.save(TestClass.class, two);
		assertEquals(db.getTableRegistration(TestClass.class).getTableClass(), TestClass.class);
		assertEquals(db.select(TestClass.class).execute().find().size(), 2);
		assertEquals(db.select(TestClass.class).where().equal("name", "Hello World").execute().findOne().name, "Hello World");
		try {
			db.close();
		} catch (ConnectionException e) {
			fail("Failed to close database! " + e.toString());
		}
		tmpfile.delete();
	}

	@Table("test")
	public static class TestClass {
		@Id
		private long id;

		@Field
		private String name;

		protected long getId() {
			return id;
		}

		protected void setId(long id) {
			this.id = id;
		}

		protected String getName() {
			return name;
		}

		protected void setName(String name) {
			this.name = name;
		}
	}

	@Table("tEsT")
	public static class TestClass2 {
		@Id
		private long id;

		@Field
		private String name;
	}
}
