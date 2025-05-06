import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import HomeScreen from './HomeScreen';
import ProfileScreen from './ProfileScreen';

const Stack = createNativeStackNavigator();

export default function App() {
  return (
    <NavigationContainer>
      <Stack.Navigator
        initialRouteName="Home"
        screenOptions={{
          headerStyle: {
            backgroundColor: '#6200ee', // Purple header
          },
          headerTintColor: '#fff', // White text
          headerTitleStyle: {
            fontWeight: 'bold',
          },
          contentStyle: {
            backgroundColor: '#f5f5f5', // Light gray screen background
          }
        }}
      >
        <Stack.Screen 
          name="Home" 
          component={HomeScreen} 
          options={{ 
            title: 'Welcome',
            // Override global styles for this screen
            headerStyle: { backgroundColor: '#3f51b5' },
          }}
        />
        <Stack.Screen 
          name="Profile" 
          component={ProfileScreen} 
          options={{ 
            title: 'User Profile',
            // Custom header color for this screen
            headerStyle: { backgroundColor: '#4caf50' },
          }}
        />
      </Stack.Navigator>
    </NavigationContainer>
  );
}